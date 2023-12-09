/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.renewpassword;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.auth.domain.otatoken.OtaTokenEntity;
import pl.visphere.auth.domain.otatoken.OtaTokenRepository;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.AccountException;
import pl.visphere.auth.exception.OtaTokenException;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.OtaTokenEmailMapper;
import pl.visphere.auth.network.renewpassword.dto.AttemptReqDto;
import pl.visphere.auth.network.renewpassword.dto.ChangeReqDto;
import pl.visphere.auth.network.renewpassword.dto.ChangeViaAccountReqDto;
import pl.visphere.auth.service.otatoken.OtaTokenService;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.async.AsyncQueueHandler;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.OtaToken;
import pl.visphere.lib.security.user.AuthUserDetails;

import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordRenewServiceImpl implements PasswordRenewService {
    private final I18nService i18nService;
    private final OtaTokenService otaTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AsyncQueueHandler asyncQueueHandler;
    private final OtaTokenEmailMapper otaTokenEmailMapper;
    private final SyncQueueHandler syncQueueHandler;

    private final UserRepository userRepository;
    private final OtaTokenRepository otaTokenRepository;

    @Override
    public BaseMessageResDto request(AttemptReqDto reqDto) {
        final UserEntity user = sendRequestForChangePassword(reqDto.getUsernameOrEmailAddress());

        log.info("Successfully send request for change password for user: '{}'", user);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.ATTEMPT_CHANGE_PASSWORD_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto verify(String token) {
        final OtaToken type = OtaToken.CHANGE_PASSWORD;
        final OtaTokenEntity otaToken = otaTokenRepository
            .findByTokenAndTypeAndIsUsedFalseAndExpiredAtAfter(token, type, ZonedDateTime.now())
            .orElseThrow(() -> new OtaTokenException.OtaTokenNotFoundException(token, type));

        if (otaTokenService.checkIfIsExpired(otaToken.getExpiredAt())) {
            log.error("Attempt to validate expired token: '{}'", otaToken);
            throw new OtaTokenException.OtaTokenNotFoundException(token, type);
        }

        log.info("Successfully validated ota token: '{}'", otaToken);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.TOKEN_VERIFICATION_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto resend(AttemptReqDto reqDto) {
        final UserEntity user = sendRequestForChangePassword(reqDto.getUsernameOrEmailAddress());

        log.info("Successfully resend email message for request change password for user: '{}'", user);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.RESEND_TOKEN_VERIFICATION_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto change(String token, ChangeReqDto reqDto) {
        final OtaToken type = OtaToken.CHANGE_PASSWORD;
        final OtaTokenEntity otaToken = otaTokenRepository
            .findByTokenAndTypeAndIsUsedFalseAndExpiredAtAfter(token, type, ZonedDateTime.now())
            .orElseThrow(() -> new OtaTokenException.OtaTokenNotFoundException(token, type));

        if (otaTokenService.checkIfIsExpired(otaToken.getExpiredAt())) {
            log.error("Attempt to change password with expired token: '{}'", otaToken);
            throw new OtaTokenException.OtaTokenNotFoundException(token, type);
        }
        final UserEntity user = otaToken.getUser();

        otaToken.setUsed(true);
        user.setPassword(passwordEncoder.encode(reqDto.getNewPassword()));

        sendEmailAfterUpdatedPassword(user);

        log.info("Successfully change password for user: '{}'", user);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CHANGE_PASSWORD_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto changeViaAccount(ChangeViaAccountReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = userRepository
            .findByLocalUsernameOrEmailAddress(user.getUsername())
            .orElseThrow(() -> new UserException.UserNotExistException(user.getUsername()));

        if (!passwordEncoder.matches(reqDto.getOldPassword(), userEntity.getPassword())) {
            throw new AccountException.IncorrectOldPasswordException(user.getUsername());
        }
        userEntity.setPassword(reqDto.getNewPassword());

        sendEmailAfterUpdatedPassword(userEntity);

        log.info("Successfully updated password via logged account for user: '{}'", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CHANGE_PASSWORD_RESPONSE_SUCCESS))
            .build();
    }

    private void sendEmailAfterUpdatedPassword(UserEntity user) {
        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(), ProfileImageDetailsResDto.class);

        final SendBaseEmailReqDto emailReqDto = otaTokenEmailMapper.mapToSendBaseEmailReq(user, profileImageDetails);
        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_PASSWORD_CHANGED, emailReqDto);
    }

    private UserEntity sendRequestForChangePassword(String username) {
        final UserEntity user = userRepository
            .findByLocalUsernameOrEmailAddress(username)
            .orElseThrow(() -> new UserException.UserNotExistException(username));

        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(), ProfileImageDetailsResDto.class);

        final GenerateOtaResDto otaResDto = otaTokenService.generate(user, OtaToken.MFA_EMAIL);
        final SendTokenEmailReqDto emailReqDto = otaTokenEmailMapper
            .mapToSendTokenEmailReq(user, otaResDto, profileImageDetails);

        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_CHANGE_PASSWORD, emailReqDto);
        return user;
    }
}
