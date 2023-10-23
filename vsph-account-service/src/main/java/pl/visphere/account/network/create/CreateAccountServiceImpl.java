/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.create;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import pl.visphere.account.domain.account.AccountEntity;
import pl.visphere.account.domain.account.AccountRepository;
import pl.visphere.account.i18n.LocaleSet;
import pl.visphere.account.network.create.dto.ActivateAccountReqDto;
import pl.visphere.account.network.create.dto.ActivateAccountResDto;
import pl.visphere.account.network.create.dto.CreateAccountReqDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.SyncQueueHandler;
import pl.visphere.lib.kafka.payload.GenerateDefaultUserProfileReqDto;
import pl.visphere.lib.kafka.payload.GenerateOtaAndSendReqDto;
import pl.visphere.lib.kafka.payload.NullableObjectWrapper;
import pl.visphere.lib.kafka.payload.PersistUserReqDto;

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
    @CacheEvict(cacheNames = { "user_by_id", "user_by_username_or_email", "user_exist_by_username_or_email" },
        allEntries = true)
    public BaseMessageResDto createNew(CreateAccountReqDto reqDto) {
        final PersistUserReqDto persistUserReqDto = modelMapper.map(reqDto, PersistUserReqDto.class);

        final Long persistUserId = syncQueueHandler
            .sendWithBlockThread(QueueTopic.PERSIST_USER, persistUserReqDto, Long.class)
            .map(NullableObjectWrapper::content)
            .orElseThrow(RuntimeException::new);

        final GenerateDefaultUserProfileReqDto profileReqDto = GenerateDefaultUserProfileReqDto.builder()
            .initials(new char[]{ reqDto.getFirstName().charAt(0), reqDto.getLastName().charAt(0) })
            .userId(persistUserId)
            .build();

        final String randomImageColor = syncQueueHandler
            .sendWithBlockThread(QueueTopic.GENERATE_DEFAULT_USER_PROFILE, profileReqDto, String.class)
            .map(NullableObjectWrapper::content)
            .orElseThrow(RuntimeException::new);

        final GenerateOtaAndSendReqDto otaReqDto = modelMapper.map(reqDto, GenerateOtaAndSendReqDto.class);
        otaReqDto.setFullName(reqDto.getFirstName() + StringUtils.SPACE + reqDto.getLastName());

        syncQueueHandler
            .sendWithBlockThread(QueueTopic.GENERATE_OTA_ACTIVATE_ACCOUNT, otaReqDto, Boolean.class)
            .orElseThrow(RuntimeException::new);

        final AccountEntity account = createAccountMapper
            .mapToAccountEntity(reqDto, persistUserId, randomImageColor);

        accountRepository.save(account);

        log.info("Successfully created new user account: {}", account);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CREATE_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public ActivateAccountResDto activate(String token, ActivateAccountReqDto reqDto) {
        // activate created account via email message

        return ActivateAccountResDto.builder()
            .message(i18nService.getMessage(LocaleSet.ACTIVATE_ACCOUNT_RESPONSE_SUCCESS))
            .mfaEnabled(false)
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
