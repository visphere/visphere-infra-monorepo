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
import pl.visphere.lib.kafka.SyncListenerHandler;
import pl.visphere.lib.kafka.payload.UserDetailsResDto;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
class AuthKafkaListener {
    private final SyncListenerHandler syncListenerHandler;

    @KafkaListener(topics = "${visphere.kafka.topic.check-user}")
    void checkUserListener(Message<String> username) {
        syncListenerHandler.parseAndSendResponse(username, data -> {
            // check, if user base username exist, if not exist, return null
            return UserDetailsResDto.builder()
                .id(1L)
                .username("annnow123")
                .password("passwordHash")
                .isNonLocked(true)
                .authorities(Set.of(AppGrantedAuthority.USER))
                .isEnabled(true)
                .build();
        });
    }

    @KafkaListener(topics = "${visphere.kafka.topic.jwt-is-on-blacklist}")
    void jwtIsOnBlaclistListener(Message<String> token) {
        syncListenerHandler.parseAndSendResponse(token, data -> {
            // check if token is in blacklist table
            return Boolean.FALSE;
        });
    }
}
