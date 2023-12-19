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
import pl.visphere.lib.kafka.payload.auth.CredentialsConfirmationReqDto;
import pl.visphere.lib.kafka.payload.auth.PersistOAuth2UserReqDto;
import pl.visphere.lib.kafka.payload.auth.UpdateOAuth2UserDetailsReqDto;
import pl.visphere.lib.kafka.sync.SyncListenerHandler;

@Slf4j
@Component
@RequiredArgsConstructor
class AuthKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final UserService userService;

    private final BlackListJwtRepository blackListJwtRepository;

    @KafkaListener(topics = "${visphere.kafka.topic.jwt-is-on-blacklist}")
    void jwtIsOnBlaclistListener(Message<String> payload) {
        syncListenerHandler.parseAndSendResponse(payload, blackListJwtRepository::existsByExpiredJwt);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.check-user}")
    void checkUserListener(Message<String> payload) {
        syncListenerHandler.parseAndSendResponse(payload, userService::checkUser);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.user-details}")
    void userDetailsListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, userService::getUserDetails);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.persist-new-user}")
    void persistNewUserListener(Message<PersistOAuth2UserReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, userService::persistNewUser);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.get-oauth2-user-details}")
    void getOAuth2UserDetailsListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, userService::getOAuth2UserDetails);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.update-oauth2-user-details}")
    void updateOAuth2UserDetailsListener(Message<UpdateOAuth2UserDetailsReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, userService::updateOAuth2UserDetails);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.login-oauth2-user}")
    void loginOAuth2UserListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, userService::loginOAuth2User);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.check-user-crendetials}")
    void checkUserCredentialsListener(Message<CredentialsConfirmationReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, userService::checkUserCredentials);
    }
}
