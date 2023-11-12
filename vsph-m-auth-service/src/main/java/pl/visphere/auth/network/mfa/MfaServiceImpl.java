/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.mfa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.auth.domain.otatoken.OtaTokenEntity;
import pl.visphere.auth.domain.otatoken.OtaTokenRepository;
import pl.visphere.auth.domain.refreshtoken.RefreshTokenEntity;
import pl.visphere.auth.domain.refreshtoken.RefreshTokenRepository;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.MfaException;
import pl.visphere.auth.exception.UserException;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.LoginResDto;
import pl.visphere.auth.network.OtaTokenEmailMapper;
import pl.visphere.auth.network.mfa.dto.MfaAuthenticatorDataResDto;
import pl.visphere.auth.network.mfa.dto.MfaCredentialsReqDto;
import pl.visphere.auth.service.mfa.MfaProxyService;
import pl.visphere.auth.service.otatoken.OtaTokenService;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.async.AsyncQueueHandler;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.OtaToken;
import pl.visphere.lib.security.user.AuthUserDetails;

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

    private final UserRepository userRepository;
    private final OtaTokenRepository otaTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public MfaAuthenticatorDataResDto authenticatorData(MfaCredentialsReqDto reqDto) {
        final UserEntity user = authenticateUser(reqDto);
        if (user.getMfaIsSetup()) {
            throw new MfaException.MfaAlreadyIsSetupException(user.getUsername());
        }
        final String mfaSecret = user.getMfaSecret();
        log.info("Successfully return MFA authenticator app details for user: '{}'", user);
        return MfaAuthenticatorDataResDto.builder()
            .imageUri(mfaProxyService.generateQrCodeUri(mfaSecret))
            .secret(mfaSecret)
            .build();
    }

    @Override
    @Transactional
    public LoginResDto authenticatorSetOrVerify(String code, MfaCredentialsReqDto reqDto, boolean isFirstTime) {
        final UserEntity user = authenticateUser(reqDto);
        if (isFirstTime) {
            user.setMfaIsSetup(true);
        }
        if (mfaProxyService.isOtpNotValid(user.getMfaSecret(), code)) {
            throw new MfaException.MfaInvalidCodeException(code, user.getUsername());
        }
        final LoginResDto resDto = createLoginResponse(user);

        log.info("Successfully authenticate with MFA via authenticator app for user: '{}'", resDto);
        return resDto;
    }

    @Override
    public BaseMessageResDto altSendEmail(MfaCredentialsReqDto reqDto) {
        final UserEntity user = authenticateUser(reqDto);

        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(), ProfileImageDetailsResDto.class);

        final GenerateOtaResDto otaResDto = otaTokenService.generate(user, OtaToken.MFA_EMAIL);
        final SendTokenEmailReqDto emailReqDto = otaTokenEmailMapper.mapToSendTokenEmailReq(user, otaResDto,
            profileImageDetails);

        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_MFA_CODE, emailReqDto);

        log.info("Successfully send message with MFA code for user: '{}'", user);
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
            .findByTokenAndTypeAndIsUsedFalse(token, type)
            .orElseThrow(() -> new MfaException.MfaInvalidCodeException(token));

        otaToken.setUsed(true);

        final UserEntity user = otaToken.getUser();
        final LoginResDto resDto = createLoginResponse(user);

        log.info("Successfully authenticate with MFA via alternative email code for user: '{}'", resDto);
        return resDto;
    }

    private UserEntity authenticateUser(MfaCredentialsReqDto reqDto) {
        final var authToken = new UsernamePasswordAuthenticationToken(reqDto.getUsernameOrEmailAddress(),
            reqDto.getPassword());
        final Authentication auth = authenticationManager.authenticate(authToken);
        final AuthUserDetails principal = (AuthUserDetails) auth.getPrincipal();
        final UserEntity user = userRepository
            .findByIdAndIsActivatedIsTrue(principal.getId())
            .orElseThrow(() -> new UserException.UserNotExistOrNotActivatedException(principal.getId()));
        if (!user.getEnabledMfa()) {
            throw new MfaException.MfaNotEnabledException(user.getUsername());
        }
        return user;
    }

    private LoginResDto createLoginResponse(UserEntity user) {
        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(), ProfileImageDetailsResDto.class);

        final TokenData access = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getEmailAddress());
        final TokenData refresh = jwtService.generateRefreshToken();

        final RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
            .refreshToken(refresh.token())
            .expiringAt(jwtService.convertToZonedDateTime(refresh.expiredAt()))
            .user(user)
            .build();
        
        refreshTokenRepository.save(refreshToken);
        return new LoginResDto(profileImageDetails.profileImagePath(), user, access.token(), refresh.token());
    }
}