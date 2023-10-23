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
import pl.visphere.lib.kafka.SyncListenerHandler;
import pl.visphere.lib.kafka.payload.GenerateDefaultUserProfileReqDto;

@Slf4j
@Component
@RequiredArgsConstructor
class MultimediaKafkaListener {
    private final SyncListenerHandler syncListenerHandler;

    @KafkaListener(topics = "${visphere.kafka.topic.generate-default-user-profile}")
    void generateDefaultUserProfileListener(Message<GenerateDefaultUserProfileReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, reqDto -> {
            // generate default initials image, before up get random background color

            final String color = "#ffffff";

            log.info("Successfully generated default profile image for user: '{}' with color: '{}'", reqDto, color);
            return color;
        });
    }
}
