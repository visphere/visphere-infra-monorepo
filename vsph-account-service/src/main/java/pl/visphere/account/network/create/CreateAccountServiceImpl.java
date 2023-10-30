/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.create;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.visphere.account.domain.account.AccountEntity;
import pl.visphere.account.domain.account.AccountRepository;
import pl.visphere.account.exception.AccountException;
import pl.visphere.account.i18n.LocaleSet;
import pl.visphere.account.network.create.dto.ActivateAccountReqDto;
import pl.visphere.account.network.create.dto.ActivateAccountResDto;
import pl.visphere.account.network.create.dto.CreateAccountReqDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.SyncQueueHandler;
import pl.visphere.lib.kafka.payload.NullableObjectWrapper;
import pl.visphere.lib.kafka.payload.SendTokenEmailReqDto;
import pl.visphere.lib.kafka.payload.auth.ActivateUserReqDto;
import pl.visphere.lib.kafka.payload.auth.ActivateUserResDto;
import pl.visphere.lib.kafka.payload.auth.CreateUserReqDto;
import pl.visphere.lib.kafka.payload.auth.CreateUserResDto;
import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileResDto;

@Slf4j
@Service
@RequiredArgsConstructor
class CreateAccountServiceImpl implements CreateAccountService {
    private final I18nService i18nService;
    private final SyncQueueHandler syncQueueHandler;
    private final ModelMapper modelMapper;
    private final CreateAccountMapper createAccountMapper;

    private final AccountRepository accountRepository;

    @Override
    public BaseMessageResDto createNew(CreateAccountReqDto reqDto) {
        final CreateUserReqDto createUserReqDto = modelMapper.map(reqDto, CreateUserReqDto.class);

        final CreateUserResDto resDto = syncQueueHandler
            .sendWithBlockThread(QueueTopic.CREATE_USER, createUserReqDto, CreateUserResDto.class)
            .map(NullableObjectWrapper::content)
            .orElseThrow(RuntimeException::new);

        final SendTokenEmailReqDto emailReqDto = createAccountMapper.mapToSendTokenEmailReq(reqDto, resDto);

        syncQueueHandler
            .sendWithBlockThread(QueueTopic.EMAIL_ACTIVATE_ACCOUNT, emailReqDto, Object.class)
            .orElseThrow(RuntimeException::new);

        final AccountEntity account = createAccountMapper.mapToAccountEntity(reqDto, resDto.userId());
        accountRepository.save(account);

        log.info("Successfully created user account: '{}'", account);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CREATE_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public ActivateAccountResDto activate(String token, ActivateAccountReqDto reqDto) {
        final ActivateUserReqDto activateUserReqDto = ActivateUserReqDto.builder()
            .token(token)
            .emailAddress(reqDto.getEmailAddress())
            .build();

        final ActivateUserResDto resDto = syncQueueHandler
            .sendWithBlockThread(QueueTopic.ACTIVATE_USER, activateUserReqDto, ActivateUserResDto.class)
            .map(NullableObjectWrapper::content)
            .orElseThrow(RuntimeException::new);

        final AccountEntity account = accountRepository.findByUserId(resDto.userId())
            .orElseThrow(() -> new AccountException.AccountNotExistException(resDto.userId()));

        final DefaultUserProfileReqDto profileReqDto = DefaultUserProfileReqDto.builder()
            .initials(new char[]{ account.getFirstName().charAt(0), account.getLastName().charAt(0) })
            .userId(resDto.userId())
            .username(resDto.username())
            .build();

        final DefaultUserProfileResDto userProfileResDto = syncQueueHandler
            .sendWithBlockThread(QueueTopic.GENERATE_DEFAULT_USER_PROFILE, profileReqDto, DefaultUserProfileResDto.class)
            .map(NullableObjectWrapper::content)
            .orElseThrow(RuntimeException::new);

        account.setProfileColor(userProfileResDto.color());
        account.setProfileImageUuid(userProfileResDto.uuid());
        accountRepository.save(account);

        log.info("Successfully activated account for user: '{}'", resDto.username());
        return ActivateAccountResDto.builder()
            .message(i18nService.getMessage(LocaleSet.ACTIVATE_ACCOUNT_RESPONSE_SUCCESS))
            .mfaEnabled(resDto.isMfaEnabled())
            .build();
    }

    @Override
    public BaseMessageResDto resend(ActivateAccountReqDto reqDto) {
        // resend email message with provided token

        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.RESEND_ACTIVATE_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }
}
