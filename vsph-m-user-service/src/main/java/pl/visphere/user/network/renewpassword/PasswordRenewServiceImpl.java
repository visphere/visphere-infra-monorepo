/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.renewpassword;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.async.AsyncQueueHandler;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.security.OtaToken;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.user.domain.otatoken.OtaTokenEntity;
import pl.visphere.user.domain.otatoken.OtaTokenRepository;
import pl.visphere.user.domain.refreshtoken.RefreshTokenEntity;
import pl.visphere.user.domain.refreshtoken.RefreshTokenRepository;
import pl.visphere.user.domain.user.UserEntity;
import pl.visphere.user.domain.user.UserRepository;
import pl.visphere.user.exception.AccountException;
import pl.visphere.user.exception.OtaTokenException;
import pl.visphere.user.i18n.LocaleSet;
import pl.visphere.user.network.OtaTokenEmailMapper;
import pl.visphere.user.network.renewpassword.dto.AttemptReqDto;
import pl.visphere.user.network.renewpassword.dto.ChangeReqDto;
import pl.visphere.user.network.renewpassword.dto.ChangeViaAccountReqDto;
import pl.visphere.user.service.otatoken.OtaTokenService;
import pl.visphere.user.service.otatoken.dto.GenerateOtaResDto;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordRenewServiceImpl implements PasswordRenewService {
    private final I18nService i18nService;
    private final OtaTokenService otaTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AsyncQueueHandler asyncQueueHandler;
    private final OtaTokenEmailMapper otaTokenEmailMapper;
    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final OtaTokenRepository otaTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public BaseMessageResDto request(AttemptReqDto reqDto) {
        final UserEntity user = sendRequestForChangePassword(reqDto.getUsernameOrEmailAddress());

        log.info("Successfully send request for change password for user: '{}'.", user);
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
            log.error("Attempt to validate expired token: '{}'.", otaToken);
            throw new OtaTokenException.OtaTokenNotFoundException(token, type);
        }

        log.info("Successfully validated ota token: '{}'.", otaToken);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.TOKEN_VERIFICATION_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto resend(AttemptReqDto reqDto) {
        final UserEntity user = sendRequestForChangePassword(reqDto.getUsernameOrEmailAddress());

        log.info("Successfully resend email message for request change password for user: '{}'.", user);
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
            log.error("Attempt to change password with expired token: '{}'.", otaToken);
            throw new OtaTokenException.OtaTokenNotFoundException(token, type);
        }
        final UserEntity user = otaToken.getUser();

        otaToken.setUsed(true);
        user.setPassword(passwordEncoder.encode(reqDto.getNewPassword()));

        sendEmailAfterUpdatedPassword(user);

        log.info("Successfully change password for user: '{}'.", user);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CHANGE_PASSWORD_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto changeViaAccount(
        HttpServletRequest req, ChangeViaAccountReqDto reqDto, AuthUserDetails user
    ) {
        final UserEntity userEntity = userRepository
            .findByLocalUsernameOrEmailAddress(user.getUsername())
            .orElseThrow(() -> new UserException.UserNotExistException(user.getUsername()));

        if (!passwordEncoder.matches(reqDto.getOldPassword(), userEntity.getPassword())) {
            throw new AccountException.IncorrectOldPasswordException(user.getUsername());
        }
        userEntity.setPassword(reqDto.getNewPassword());

        if (reqDto.isLogoutFromAll()) {
            final String refreshToken = jwtService.extractRefreshFromReq(req);
            final List<RefreshTokenEntity> activeTokens = refreshTokenRepository
                .findAllByUser_IdAndRefreshTokenNot(user.getId(), refreshToken);
            refreshTokenRepository.deleteAll(activeTokens);
            log.info("Successfully terminated user sessions count: '{}'.", activeTokens.size());
        }
        sendEmailAfterUpdatedPassword(userEntity);

        log.info("Successfully updated password via logged account for user: '{}'.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CHANGE_PASSWORD_RESPONSE_SUCCESS))
            .build();
    }

    private void sendEmailAfterUpdatedPassword(UserEntity user) {
        final SendBaseEmailReqDto emailReqDto = otaTokenEmailMapper.mapToSendBaseEmailReq(user);
        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_PASSWORD_CHANGED, emailReqDto);
    }

    private UserEntity sendRequestForChangePassword(String username) {
        final UserEntity user = userRepository
            .findByLocalUsernameOrEmailAddress(username)
            .orElseThrow(() -> new UserException.UserNotExistException(username));

        final GenerateOtaResDto otaResDto = otaTokenService.generate(user, OtaToken.MFA_EMAIL);
        final SendTokenEmailReqDto emailReqDto = otaTokenEmailMapper
            .mapToSendTokenEmailReq(user, otaResDto);

        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_CHANGE_PASSWORD, emailReqDto);
        return user;
    }
}
