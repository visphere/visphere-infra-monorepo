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
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsReqDto;
import pl.visphere.lib.kafka.sync.SyncListenerHandler;
import pl.visphere.sphere.service.sphereguild.SphereGuildService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SphereKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final SphereGuildService sphereGuildService;

    @KafkaListener(topics = "${visphere.kafka.topic.get-guild-details}")
    void sendForActivateAccountListener(Message<GuildDetailsReqDto> reqDto) {
        syncListenerHandler.parseAndSendResponse(reqDto, sphereGuildService::getGuildDetails);
    }
}
