/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.visphere.account.service.accout.AccountService;
import pl.visphere.lib.kafka.SyncListenerHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final AccountService accountService;

    @KafkaListener(topics = "${visphere.kafka.topic.account-details}")
    void jwtIsOnBlaclistListener(Message<Long> userId) {
        syncListenerHandler.parseAndSendResponse(userId, accountService::getAccountDetails);
    }
}
