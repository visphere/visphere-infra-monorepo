/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.identity;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.visphere.auth.domain.blacklistjwt.BlackListJwtEntity;
import pl.visphere.auth.domain.blacklistjwt.BlackListJwtRepository;
import pl.visphere.auth.domain.refreshtoken.RefreshTokenEntity;
import pl.visphere.auth.domain.refreshtoken.RefreshTokenRepository;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.RefrehTokenException;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.LoginResDto;
import pl.visphere.auth.network.identity.dto.LoginPasswordReqDto;
import pl.visphere.auth.network.identity.dto.RefreshReqDto;
import pl.visphere.auth.network.identity.dto.RefreshResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtClaim;
import pl.visphere.lib.jwt.JwtException;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2DetailsResDto;
import pl.visphere.lib.kafka.payload.settings.UserSettingsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.user.AuthUserDetails;

import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
class IdentityServiceImpl implements IdentityService {
    private final I18nService i18nService;
    private final JwtService jwtService;
    private final SyncQueueHandler syncQueueHandler;
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final BlackListJwtRepository blackListJwtRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public LoginResDto loginViaPassword(LoginPasswordReqDto reqDto) {
        final var authToken = new UsernamePasswordAuthenticationToken(reqDto.getUsernameOrEmailAddress(),
            reqDto.getPassword());

        final Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        final AuthUserDetails principal = (AuthUserDetails) auth.getPrincipal();

        final UserEntity user = userRepository
            .findById(principal.getId())
            .orElseThrow(() -> new UserException.UserNotExistException(principal.getId()));

        String token = StringUtils.EMPTY;
        String refreshToken = StringUtils.EMPTY;
        if (user.getIsActivated() && user.getMfaUser() == null) {
            token = generateToken(user);
            if (!user.getIsDisabled()) {
                final TokenData generateRefreshToken = jwtService.generateRefreshToken();
                final RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                    .refreshToken(generateRefreshToken.token())
                    .expiringAt(jwtService.convertToZonedDateTime(generateRefreshToken.expiredAt()))
                    .user(user)
                    .build();
                refreshToken = generateRefreshToken.token();
                refreshTokenRepository.save(refreshTokenEntity);
            }
        }

        String userProfileUrl = StringUtils.EMPTY;
        String userProfileColor = StringUtils.EMPTY;
        UserSettingsResDto settingsResDto = new UserSettingsResDto();

        if (user.getIsActivated()) {
            final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(),
                    ProfileImageDetailsResDto.class);
            userProfileUrl = profileImageDetails.profileImagePath();
            userProfileColor = profileImageDetails.profileColor();
            settingsResDto = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.GET_USER_PERSISTED_RELATED_SETTINGS, user.getId(),
                    UserSettingsResDto.class);
        }
        final LoginResDto resDto = new LoginResDto(userProfileUrl, userProfileColor, user, token, refreshToken,
            settingsResDto);

        log.info("Successfully login via username and password for user: '{}'.", resDto);
        return resDto;
    }

    @Override
    public LoginResDto loginViaAccessToken(HttpServletRequest req, AuthUserDetails userDetails) {
        final String accessToken = jwtService.extractFromReq(req);
        final String refreshToken = jwtService.extractRefreshFromReq(req);

        final UserEntity user = userRepository
            .findById(userDetails.getId())
            .orElseThrow(() -> new UserException.UserNotExistException(userDetails.getId()));
        if (user.getIsDisabled()) {
            throw new UserException.UserAccountDisabledException(user.getUsername());
        }
        boolean hasCustomImage = true;
        String profileImagePath = StringUtils.EMPTY;

        if (user.getExternalCredProvider()) {
            final OAuth2DetailsResDto detailsResDto = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.GET_OAUTH2_DETAILS, user.getId(), OAuth2DetailsResDto.class);

            hasCustomImage = !detailsResDto.profileImageSuppliedByProvider();
            profileImagePath = detailsResDto.profileImageUrl();
        }
        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler.sendNotNullWithBlockThread(
            QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(), ProfileImageDetailsResDto.class);

        if (hasCustomImage) {
            profileImagePath = profileImageDetails.profileImagePath();
        }
        final RefreshTokenEntity refreshTokenEntity = refreshTokenRepository
            .findByRefreshTokenAndUserId(refreshToken, user.getId())
            .orElseThrow(() -> new RefrehTokenException.RefreshTokenExpiredException(refreshToken));

        final UserSettingsResDto settingsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_USER_PERSISTED_RELATED_SETTINGS, user.getId(),
                UserSettingsResDto.class);

        final LoginResDto resDto = new LoginResDto(profileImagePath, profileImageDetails.profileColor(),
            user, accessToken, refreshTokenEntity.getRefreshToken(), settingsResDto);

        log.info("Successfully login via access token for user: '{}'.", resDto);
        return resDto;
    }

    @Override
    public RefreshResDto refresh(RefreshReqDto reqDto) {
        final Claims claims = jwtService
            .extractClaims(reqDto.getExpiredAccessToken())
            .orElseThrow(JwtException.JwtGeneralException::new);

        final Long userId = jwtService.getClaim(claims, JwtClaim.USER_ID, Long.class);
        final UserEntity user = userRepository
            .findById(userId)
            .orElseThrow(() -> new UserException.UserNotExistException(userId));
        if (user.getIsDisabled()) {
            throw new UserException.UserAccountDisabledException(user.getUsername());
        }
        final RefreshTokenEntity refreshToken = refreshTokenRepository
            .findByRefreshTokenAndUserId(reqDto.getRefreshToken(), user.getId())
            .orElseThrow(() -> new RefrehTokenException.RefreshTokenExpiredException(reqDto.getRefreshToken()));

        log.info("Successfully refresh expired access token for user: '{}'.", user);
        return RefreshResDto.builder()
            .renewAccessToken(generateToken(user))
            .refreshToken(refreshToken.getRefreshToken())
            .build();
    }

    @Override
    public BaseMessageResDto logout(HttpServletRequest req, AuthUserDetails userDetails) {
        final String accessToken = jwtService.extractFromReq(req);
        final String refreshToken = jwtService.extractRefreshFromReq(req);

        final Claims claims = jwtService
            .extractClaims(accessToken)
            .orElseThrow(JwtException.JwtGeneralException::new);

        final ZonedDateTime expiredAt = jwtService.convertToZonedDateTime(claims.getExpiration());
        final UserEntity user = userRepository
            .findById(userDetails.getId())
            .orElseThrow(() -> new UserException.UserNotExistException(userDetails.getId()));

        final BlackListJwtEntity blackListJwt = new BlackListJwtEntity(accessToken, expiredAt);
        blackListJwt.setUser(user);
        blackListJwtRepository.save(blackListJwt);

        final RefreshTokenEntity refreshTokenEntity = refreshTokenRepository
            .findByRefreshTokenAndUserId(refreshToken, user.getId())
            .orElseThrow(() -> new RefrehTokenException.RefreshTokenNotFoundException(refreshToken));

        refreshTokenRepository.delete(refreshTokenEntity);
        SecurityContextHolder.clearContext();

        log.info("Successfully logged out user: '{}'.", user);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.LOGOUT_RESPONSE_SUCCESS))
            .build();
    }

    private String generateToken(UserEntity user) {
        return jwtService
            .generateAccessToken(user.getId(), user.getUsername(), user.getEmailAddress())
            .token();
    }
}
