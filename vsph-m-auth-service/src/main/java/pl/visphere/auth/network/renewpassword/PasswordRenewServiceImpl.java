/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.renewpassword;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.visphere.auth.domain.otatoken.OtaTokenEntity;
import pl.visphere.auth.domain.otatoken.OtaTokenRepository;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.OtaTokenException;
import pl.visphere.auth.exception.UserException;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.renewpassword.dto.AttemptReqDto;
import pl.visphere.auth.network.renewpassword.dto.ChangeReqDto;
import pl.visphere.auth.service.otatoken.OtaTokenService;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.security.OtaToken;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordRenewServiceImpl implements PasswordRenewService {
    private final I18nService i18nService;
    private final OtaTokenService otaTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AsyncQueueHandler asyncQueueHandler;
    private final RenewPasswordMapper renewPasswordMapper;

    private final UserRepository userRepository;
    private final OtaTokenRepository otaTokenRepository;

    @Override
    public BaseMessageResDto request(AttemptReqDto reqDto) {
        final UserEntity user = userRepository
            .findByUsernameOrEmailAddress(reqDto.getUsernameOrEmailAddress())
            .orElseThrow(() -> new UserException.UserNotExistException(reqDto.getUsernameOrEmailAddress()));

        final GenerateOtaResDto otaResDto = otaTokenService.generate(user, OtaToken.CHANGE_PASSWORD);
        final SendTokenEmailReqDto emailReqDto = renewPasswordMapper
            .mapToSendTokenEmailReq(user, otaResDto);

        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_CHANGE_PASSWORD, emailReqDto);

        log.info("Successfully send request for change password for user: '{}'", user);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.ATTEMPT_CHANGE_PASSWORD_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto verify(String token) {
        final OtaToken type = OtaToken.CHANGE_PASSWORD;
        final OtaTokenEntity otaToken = otaTokenRepository
            .findTokenByType(token, type)
            .orElseThrow(() -> new OtaTokenException.OtaTokenNotFoundException(token, type));

        log.info("Successfully validated ota token: '{}'", otaToken);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.TOKEN_VERIFICATION_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto resend(AttemptReqDto reqDto) {
        // resend email message with created previously token

        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.RESEND_TOKEN_VERIFICATION_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto change(String token, ChangeReqDto reqDto) {
        final OtaToken type = OtaToken.CHANGE_PASSWORD;
        final OtaTokenEntity otaToken = otaTokenRepository
            .findTokenByType(token, type)
            .orElseThrow(() -> new OtaTokenException.OtaTokenNotFoundException(token, type));

        final UserEntity user = otaToken.getUser();

        user.setPassword(passwordEncoder.encode(reqDto.getNewPassword()));
        final UserEntity savedUser = userRepository.save(user);

        final SendBaseEmailReqDto emailReqDto = renewPasswordMapper.mapToSendBaseEmailReq(user);
        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_NEW_ACCOUNT, emailReqDto);

        log.info("Successfully change password for user: '{}'", savedUser);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CHANGE_PASSWORD_RESPONSE_SUCCESS))
            .build();
    }
}
