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
import pl.visphere.auth.exception.UserException;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.identity.dto.LoginPasswordReqDto;
import pl.visphere.auth.network.identity.dto.LoginResDto;
import pl.visphere.auth.network.identity.dto.RefreshReqDto;
import pl.visphere.auth.network.identity.dto.RefreshResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtClaim;
import pl.visphere.lib.jwt.JwtException;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.SyncQueueHandler;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
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
    private final IdentityMapper identityMapper;

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

        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(), ProfileImageDetailsResDto.class);

        String token = StringUtils.EMPTY;
        String refreshToken = StringUtils.EMPTY;
        if (user.getActivated() && !user.getEnabledMfa()) {
            token = generateToken(user);
            final TokenData generateRefreshToken = jwtService.generateRefreshToken();
            final RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .refreshToken(generateRefreshToken.token())
                .expiringAt(jwtService.convertToZonedDateTime(generateRefreshToken.expiredAt()))
                .user(user)
                .build();
            refreshToken = generateRefreshToken.token();
            refreshTokenRepository.save(refreshTokenEntity);
        }
        final LoginResDto resDto = identityMapper.mapToLoginResDto(profileImageDetails, user, token, refreshToken);

        log.info("Successfully login via username and password for user: '{}'", resDto);
        return resDto;
    }

    @Override
    public LoginResDto loginViaAccessToken(HttpServletRequest req, AuthUserDetails userDetails) {
        final String accessToken = jwtService.extractFromReq(req);
        final String refreshToken = jwtService.extractRefreshFromReq(req);

        final UserEntity user = userRepository
            .findById(userDetails.getId())
            .orElseThrow(() -> new UserException.UserNotExistException(userDetails.getId()));

        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(), ProfileImageDetailsResDto.class);

        final RefreshTokenEntity refreshTokenEntity = refreshTokenRepository
            .findByRefreshTokenAndUserId(refreshToken, user.getId())
            .orElseThrow(() -> new RefrehTokenException.RefreshTokenExpiredException(refreshToken));

        final LoginResDto resDto = identityMapper
            .mapToLoginResDto(profileImageDetails, user, accessToken, refreshTokenEntity.getRefreshToken());

        log.info("Successfully login via access token for user: '{}'", resDto);
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

        final RefreshTokenEntity refreshToken = refreshTokenRepository
            .findByRefreshTokenAndUserId(reqDto.getRefreshToken(), user.getId())
            .orElseThrow(() -> new RefrehTokenException.RefreshTokenExpiredException(reqDto.getRefreshToken()));

        log.info("Successfully refresh expired access token for user: '{}'", user);
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

        log.info("Successfully logged out user: '{}'", user);
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