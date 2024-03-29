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
import pl.visphere.lib.kafka.payload.multimedia.*;
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

    @KafkaListener(topics = "${visphere.kafka.topic.update-default-user-profile}")
    void updateDefaultUserProfileListener(Message<UpdateUserProfileReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::updateDefaultProfile);
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
    void getProfileImageDetailsListener(Message<ProfileImageDetailsReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::getProfileImageDetails);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.replace-profile-image-with-locked}")
    void replaceProfileImageWithLockedListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::replaceProfileWithLocked);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.replace-locked-with-profile-image}")
    void replaceLockedWithProfileImageListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::replaceLockedWithProfile);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.get-guild-profile-image-details}")
    void getGuildProfileImageDetailsListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::getGuildProfileImageDetails);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.get-guild-images-by-guild-ids}")
    void getGuildImagesByGuildIdsListener(Message<GuildImageByIdsReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::getGuildImagesByGuildIds);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.delete-user-image-data}")
    void deleteUserImageDataListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::deleteUserImageData);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.delete-guild-image-data}")
    void deleteGuildImageDataListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::deleteGuildImageData);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.get-users-images-details}")
    void getUsersImagesDetailsListener(Message<UsersImagesDetailsReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, imageService::getUsersImagesDetails);
    }
}
