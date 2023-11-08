/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.auth.domain.otatoken.OtaTokenEntity;
import pl.visphere.auth.domain.otatoken.OtaTokenRepository;
import pl.visphere.auth.domain.role.RoleEntity;
import pl.visphere.auth.domain.role.RoleRepository;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.OtaTokenException;
import pl.visphere.auth.exception.RoleException;
import pl.visphere.auth.exception.UserException;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.account.dto.ActivateAccountReqDto;
import pl.visphere.auth.network.account.dto.ActivateAccountResDto;
import pl.visphere.auth.network.account.dto.CreateAccountReqDto;
import pl.visphere.auth.service.otatoken.OtaTokenService;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.async.AsyncQueueHandler;
import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileReqDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.security.OtaToken;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {
    private final I18nService i18nService;
    private final SyncQueueHandler syncQueueHandler;
    private final ModelMapper modelMapper;
    private final AccountMapper accountMapper;
    private final AsyncQueueHandler asyncQueueHandler;
    private final PasswordEncoder passwordEncoder;
    private final OtaTokenService otaTokenService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtaTokenRepository otaTokenRepository;

    @Override
    @Transactional
    public BaseMessageResDto createNew(CreateAccountReqDto reqDto) {
        if (userRepository.existsByUsernameOrEmailAddress(reqDto.getUsername(), reqDto.getEmailAddress())) {
            throw new UserException.UserAlreadyExistException(reqDto.getUsername(), reqDto.getEmailAddress());
        }
        final RoleEntity role = roleRepository
            .findByRole(AppGrantedAuthority.USER)
            .orElseThrow(() -> new RoleException.RoleNotExistException(AppGrantedAuthority.USER));

        final UserEntity user = modelMapper.map(reqDto, UserEntity.class);
        if (reqDto.getSecondEmailAddress().equals(StringUtils.EMPTY)) {
            user.setSecondEmailAddress(null);
        }
        user.setPassword(passwordEncoder.encode(reqDto.getPassword()));
        user.setBirthDate(LocalDate.parse(reqDto.getBirthDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        user.addRole(role);

        // TODO: save global notification settings in notifications microservice table

        final UserEntity savedUser = userRepository.save(user);
        final GenerateOtaResDto otaResDto = otaTokenService.generate(savedUser, OtaToken.ACTIVATE_ACCOUNT);

        final SendTokenEmailReqDto emailReqDto = accountMapper.mapToSendTokenEmailReq(reqDto, otaResDto);
        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_ACTIVATE_ACCOUNT, emailReqDto);

        log.info("Successfully created user account: '{}'", savedUser);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CREATE_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public ActivateAccountResDto activate(String token, ActivateAccountReqDto reqDto) {
        final UserEntity user = userRepository
            .findByUsernameOrEmailAddress(reqDto.getEmailAddress())
            .orElseThrow(() -> new UserException.UserNotExistException(reqDto.getEmailAddress()));

        if (user.getActivated()) {
            throw new UserException.UserAlreadyActivatedException(user);
        }

        final OtaToken type = OtaToken.ACTIVATE_ACCOUNT;
        final OtaTokenEntity otaToken = otaTokenRepository
            .findTokenByTypeAndUserId(token, type, user.getId())
            .orElseThrow(() -> new OtaTokenException.OtaTokenNotFoundException(token, type));

        otaToken.setUsed(true);
        user.setActivated(true);
        final UserEntity activatedUser = userRepository.save(user);

        final DefaultUserProfileReqDto profileReqDto = DefaultUserProfileReqDto.builder()
            .initials(new char[]{ user.getFirstName().charAt(0), user.getLastName().charAt(0) })
            .userId(user.getId())
            .username(user.getUsername())
            .build();

        syncQueueHandler.sendEmptyWithBlockThread(QueueTopic.GENERATE_DEFAULT_USER_PROFILE, profileReqDto);

        final SendBaseEmailReqDto emailReqDto = accountMapper.mapToSendBaseEmailReq(activatedUser);
        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_NEW_ACCOUNT, emailReqDto);

        log.info("Successfully activated account for user: '{}'", activatedUser);
        return ActivateAccountResDto.builder()
            .message(i18nService.getMessage(LocaleSet.ACTIVATE_ACCOUNT_RESPONSE_SUCCESS))
            .mfaEnabled(user.getEnabledMfa())
            .build();
    }

    @Override
    public BaseMessageResDto resend(ActivateAccountReqDto reqDto) {
        // resend email message with provided token

        log.info("Successfully resend activate account message");
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.RESEND_ACTIVATE_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }
}
