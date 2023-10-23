/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.visphere.account.domain.account.AccountEntity;
import pl.visphere.account.domain.account.AccountRepository;
import pl.visphere.account.exception.AccountException;
import pl.visphere.lib.kafka.SyncListenerHandler;
import pl.visphere.lib.kafka.payload.AccountDetailsResDto;

import java.util.StringJoiner;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final AccountRepository accountRepository;

    @Value("${visphere.s3.host}")
    private String s3Host;

    @KafkaListener(topics = "${visphere.kafka.topic.account-details}")
    void jwtIsOnBlaclistListener(Message<Long> userId) {
        syncListenerHandler.parseAndSendResponse(userId, id -> {
            final AccountEntity account = accountRepository
                .findByUserId(id)
                .orElseThrow(() -> new AccountException.AccountNotExistException(id));

            final StringJoiner joiner = new StringJoiner(StringUtils.SPACE);
            final String fullName = joiner.add(account.getFirstName()).add(account.getLastName()).toString();

            return AccountDetailsResDto.builder()
                .fullName(fullName)
                .profileUrl(String.format("%s/users/user-%s/profile.png", s3Host, id))
                .build();
        });
    }
}
