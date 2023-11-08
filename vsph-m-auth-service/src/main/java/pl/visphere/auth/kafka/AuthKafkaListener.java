/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.visphere.auth.domain.blacklistjwt.BlackListJwtRepository;
import pl.visphere.auth.service.user.UserService;
import pl.visphere.lib.kafka.sync.SyncListenerHandler;

@Slf4j
@Component
@RequiredArgsConstructor
class AuthKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final UserService userService;

    private final BlackListJwtRepository blackListJwtRepository;

    @KafkaListener(topics = "${visphere.kafka.topic.jwt-is-on-blacklist}")
    void jwtIsOnBlaclistListener(Message<String> token) {
        syncListenerHandler.parseAndSendResponse(token, blackListJwtRepository::existsByExpiredJwt);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.check-user}")
    void checkUserListener(Message<String> username) {
        syncListenerHandler.parseAndSendResponse(username, userService::checkUser);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.user-details}")
    void userDetailsListener(Message<Long> userId) {
        syncListenerHandler.parseAndSendResponse(userId, userService::getUserDetails);
    }
}
