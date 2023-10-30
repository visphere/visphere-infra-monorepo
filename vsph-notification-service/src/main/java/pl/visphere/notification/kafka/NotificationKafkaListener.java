/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.SyncListenerHandler;
import pl.visphere.lib.kafka.payload.SendTokenEmailReqDto;

@Slf4j
@Component
@RequiredArgsConstructor
class NotificationKafkaListener {
    private final SyncListenerHandler syncListenerHandler;

    @KafkaListener(topics = "${visphere.kafka.topic.email-activate-account}")
    void sendForActivateAccount(Message<SendTokenEmailReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, reqDto -> {
            // send email message

            return null;
        });
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-change-password}")
    void sendForChangePassword(Message<SendTokenEmailReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, reqDto -> {
            // send email message

            return null;
        });
    }
}
