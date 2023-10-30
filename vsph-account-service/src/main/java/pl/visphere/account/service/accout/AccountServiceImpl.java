/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.service.accout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pl.visphere.account.domain.account.AccountEntity;
import pl.visphere.account.domain.account.AccountRepository;
import pl.visphere.account.exception.AccountException;
import pl.visphere.lib.kafka.payload.account.AccountDetailsResDto;
import pl.visphere.lib.s3.S3Helper;

import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final S3Helper s3Helper;

    private final AccountRepository accountRepository;

    @Override
    public AccountDetailsResDto getAccountDetails(Long userId) {
        final AccountEntity account = accountRepository
            .findByUserId(userId)
            .orElseThrow(() -> new AccountException.AccountNotExistException(userId));

        final String fullName = new StringJoiner(StringUtils.SPACE)
            .add(account.getFirstName())
            .add(account.getLastName())
            .toString();

        final AccountDetailsResDto resDto = AccountDetailsResDto.builder()
            .fullName(fullName)
            .profileUrl(s3Helper.prepareUserProfilePath(userId, account.getProfileImageUuid()))
            .build();

        log.info("Successfully get account details data: '{}'", resDto);
        return resDto;
    }
}
