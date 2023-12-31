/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2UsersDetailsReqDto;
import pl.visphere.lib.kafka.sync.SyncListenerHandler;
import pl.visphere.oauth2.service.oauth2service.OAuth2Service;

@Slf4j
@Component
@RequiredArgsConstructor
class OAuth2ClientKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final OAuth2Service oAuth2Service;

    @KafkaListener(topics = "${visphere.kafka.topic.get-oauth2-details}")
    void getOAuth2DetailsListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, oAuth2Service::getOAuthDetails);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.delete-oauth2-user-data}")
    void deleteOAuth2UserDataListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, oAuth2Service::deleteOAuth2UserData);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.get-oauth2-users-details}")
    void getOAuth2UsersDetailsListener(Message<OAuth2UsersDetailsReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, oAuth2Service::getOAuthUsersDetails);
    }
}
