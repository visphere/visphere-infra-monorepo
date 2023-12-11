/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.mfa;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.auth.cache.CacheName;
import pl.visphere.auth.domain.mfauser.MfaUserEntity;
import pl.visphere.auth.domain.mfauser.MfaUserRepository;
import pl.visphere.auth.domain.otatoken.OtaTokenEntity;
import pl.visphere.auth.domain.otatoken.OtaTokenRepository;
import pl.visphere.auth.domain.refreshtoken.RefreshTokenEntity;
import pl.visphere.auth.domain.refreshtoken.RefreshTokenRepository;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.MfaException;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.LoginResDto;
import pl.visphere.auth.network.OtaTokenEmailMapper;
import pl.visphere.auth.network.mfa.dto.MfaAuthenticatorDataResDto;
import pl.visphere.auth.network.mfa.dto.MfaCredentialsReqDto;
import pl.visphere.auth.service.mfa.MfaProxyService;
import pl.visphere.auth.service.otatoken.OtaTokenService;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.async.AsyncQueueHandler;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendStateEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.kafka.payload.settings.UserSettingsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.OtaToken;
import pl.visphere.lib.security.user.AuthUserDetails;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class MfaServiceImpl implements MfaService {
    private final I18nService i18nService;
    private final AuthenticationManager authenticationManager;
    private final MfaProxyService mfaProxyService;
    private final SyncQueueHandler syncQueueHandler;
    private final JwtService jwtService;
    private final OtaTokenService otaTokenService;
    private final AsyncQueueHandler asyncQueueHandler;
    private final OtaTokenEmailMapper otaTokenEmailMapper;
    private final CacheService cacheService;
    private final MfaMapper mfaMapper;

    private final UserRepository userRepository;
    private final OtaTokenRepository otaTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MfaUserRepository mfaUserRepository;

    @Override
    public MfaAuthenticatorDataResDto authenticatorData(MfaCredentialsReqDto reqDto) {
        final UserEntity user = authenticateUser(reqDto);
        final MfaUserEntity mfaUser = user.getMfaUser();
        if (mfaUser.getMfaIsSetup()) {
            throw new MfaException.MfaAlreadyIsSetupException(user.getUsername());
        }
        final String mfaSecret = mfaUser.getMfaSecret();
        log.info("Successfully return MFA authenticator app details for user: '{}'.", user);
        return MfaAuthenticatorDataResDto.builder()
            .imageUri(mfaProxyService.generateQrCodeUri(mfaSecret))
            .secret(mfaSecret)
            .build();
    }

    @Override
    @Transactional
    public LoginResDto authenticatorSetOrVerify(String code, MfaCredentialsReqDto reqDto, boolean isFirstTime) {
        final UserEntity user = authenticateUser(reqDto);
        final MfaUserEntity mfaUser = user.getMfaUser();
        if (isFirstTime) {
            mfaUser.setMfaIsSetup(true);
        }
        if (mfaProxyService.isOtpNotValid(mfaUser.getMfaSecret(), code)) {
            throw new MfaException.MfaInvalidCodeException(code, user.getUsername());
        }
        final LoginResDto resDto = createLoginResponse(user);
        cacheService.deleteCache(CacheName.USER_ENTITY_USER_ID, user.getId());

        log.info("Successfully authenticate with MFA via authenticator app for user: '{}'.", resDto);
        return resDto;
    }

    @Override
    public BaseMessageResDto altSendEmail(MfaCredentialsReqDto reqDto) {
        final UserEntity user = authenticateUser(reqDto);

        final GenerateOtaResDto otaResDto = otaTokenService.generate(user, OtaToken.MFA_EMAIL);
        final SendTokenEmailReqDto emailReqDto = otaTokenEmailMapper.mapToSendTokenEmailReq(user, otaResDto);

        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_MFA_CODE, emailReqDto);

        log.info("Successfully send message with MFA code for user: '{}'.", user);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SEND_ALT_MFA_EMAIL_CODE_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public LoginResDto altVerifyEmailToken(String token, MfaCredentialsReqDto reqDto) {
        final OtaToken type = OtaToken.MFA_EMAIL;
        final var authToken = new UsernamePasswordAuthenticationToken(reqDto.getUsernameOrEmailAddress(),
            reqDto.getPassword());

        authenticationManager.authenticate(authToken);

        final OtaTokenEntity otaToken = otaTokenRepository
            .findByTokenAndTypeAndIsUsedFalseAndExpiredAtAfter(token, type, ZonedDateTime.now())
            .orElseThrow(() -> new MfaException.MfaInvalidCodeException(token));

        otaToken.setUsed(true);

        final UserEntity user = otaToken.getUser();
        final LoginResDto resDto = createLoginResponse(user);

        log.info("Successfully authenticate with MFA via alternative email code for user: '{}'.", resDto);
        return resDto;
    }

    @Override
    @Transactional
    public BaseMessageResDto toggleMfaAccountState(boolean isEnabled, AuthUserDetails user) {
        final UserEntity userEntity = userRepository
            .findByLocalUsernameOrEmailAddress(user.getUsername())
            .orElseThrow(() -> new UserException.UserNotExistException(user.getUsername()));

        final MfaUserEntity mfaUser = userEntity.getMfaUser();
        if (isEnabled) {
            if (mfaUser != null) {
                throw new MfaException.MfaCurrentlyEnabledException(user.getUsername());
            }
            final MfaUserEntity mfaUserEntity = MfaUserEntity.builder()
                .mfaSecret(mfaProxyService.generateSecret())
                .build();
            userEntity.persistMfaUser(mfaUserEntity);
        } else {
            if (mfaUser == null) {
                throw new MfaException.MfaCurrentlyDisabledException(user.getUsername());
            }
            mfaUserRepository.deleteById(mfaUser.getId());
            userEntity.setMfaUser(null);
        }
        cacheService.deleteCache(CacheName.USER_ENTITY_USER_ID, userEntity.getId());

        final SendStateEmailReqDto emailReqDto = mfaMapper.mapToSendStateEmailReq(userEntity, isEnabled);
        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_UPDATED_MFA_STATE, emailReqDto);

        log.info("MFA settings updated for user: '{}' with value: '{}'.", userEntity, isEnabled);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.MFA_UPDATE_SETTINGS_RESPONSE_SUCCESS))
            .build();
    }

    private UserEntity authenticateUser(MfaCredentialsReqDto reqDto) {
        final var authToken = new UsernamePasswordAuthenticationToken(reqDto.getUsernameOrEmailAddress(),
            reqDto.getPassword());
        final Authentication auth = authenticationManager.authenticate(authToken);
        final AuthUserDetails principal = (AuthUserDetails) auth.getPrincipal();
        final UserEntity user = userRepository
            .findByIdAndIsActivatedIsTrue(principal.getId())
            .orElseThrow(() -> new UserException.UserNotExistOrNotActivatedException(principal.getId()));
        if (user.getMfaUser() == null) {
            throw new MfaException.MfaNotEnabledException(user.getUsername());
        }
        return user;
    }

    private LoginResDto createLoginResponse(UserEntity user) {
        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(), ProfileImageDetailsResDto.class);

        final TokenData access = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getEmailAddress());

        final UserSettingsResDto settingsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_USER_PERSISTED_RELATED_SETTINGS, user.getId(),
                UserSettingsResDto.class);

        String refreshToken = StringUtils.EMPTY;
        if (!user.getIsDisabled()) {
            final TokenData refresh = jwtService.generateRefreshToken();

            final RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .refreshToken(refresh.token())
                .expiringAt(jwtService.convertToZonedDateTime(refresh.expiredAt()))
                .user(user)
                .build();
            refreshTokenRepository.save(refreshTokenEntity);
            refreshToken = refresh.token();
        }
        return new LoginResDto(profileImageDetails.profileImagePath(), profileImageDetails.profileColor(), user,
            access.token(), refreshToken, settingsResDto);
    }
}
