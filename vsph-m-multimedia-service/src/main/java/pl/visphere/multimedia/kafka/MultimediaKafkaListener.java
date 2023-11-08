/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.payload.multimedia.DefaultGuildProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileReqDto;
import pl.visphere.lib.kafka.sync.SyncListenerHandler;
import pl.visphere.multimedia.service.image.ImageService;

@Slf4j
@Component
@RequiredArgsConstructor
class MultimediaKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final ImageService imageService;

    @KafkaListener(topics = "${visphere.kafka.topic.generate-default-user-profile}")
    void generateDefaultUserProfileListener(Message<DefaultUserProfileReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::generateDefaultProfile);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.generate-default-guild-profile}")
    void generateDefaultGuildProfileListener(Message<DefaultGuildProfileReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::generateDefaultGuildProfile);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.update-default-guild-profile}")
    void updateDefaultGuildProfileListener(Message<DefaultGuildProfileReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::updateDefaultGuildProfile);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.profile-image-details}")
    void getProfileImageDetails(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::getProfileImageDetails);
    }
}
