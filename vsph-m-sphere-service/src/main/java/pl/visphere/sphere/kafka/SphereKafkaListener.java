/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.payload.sphere.GuildAssignmentsReqDto;
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsReqDto;
import pl.visphere.lib.kafka.payload.sphere.TextChannelAssignmentsReqDto;
import pl.visphere.lib.kafka.sync.SyncListenerHandler;
import pl.visphere.sphere.service.sphereguild.SphereGuildService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SphereKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final SphereGuildService sphereGuildService;

    @KafkaListener(topics = "${visphere.kafka.topic.get-guild-details}")
    void getGuildDetailsListener(Message<GuildDetailsReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, sphereGuildService::getGuildDetails);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.check-user-sphere-guilds}")
    void checkUserSphereGuildsListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, sphereGuildService::checkUserSphereGuilds);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.check-user-guild-assignments}")
    void checkGuildAssignmentsListener(Message<GuildAssignmentsReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, sphereGuildService::checkUserGuildAssignments);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.check-text-channel-assignments}")
    void checkTextChannelAssignmentsListener(Message<TextChannelAssignmentsReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, sphereGuildService::checkTextChannelAssignments);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.delete-user-from-guilds}")
    void deleteUserFromGuildsListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, sphereGuildService::deleteUserFromGuilds);
    }
}
