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
import pl.visphere.lib.kafka.payload.CheckOtaReqDto;
import pl.visphere.lib.kafka.payload.GenerateOtaAndSendReqDto;

@Slf4j
@Component
@RequiredArgsConstructor
class NotificationKafkaListener {
    private final SyncListenerHandler syncListenerHandler;

    @KafkaListener(topics = "${visphere.kafka.topic.generate-ota-activate-account}")
    void generateOtaAndSendForActivateAccount(Message<GenerateOtaAndSendReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, reqDto -> {
            // generate ota token, save in db, send email message

            return true;
        });
    }

    @KafkaListener(topics = "${visphere.kafka.topic.generate-ota-change-password}")
    void generateOtaAndSendForChangePassword(Message<GenerateOtaAndSendReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, reqDto -> {
            // generate ota token, save in db, send email message

            return true;
        });
    }

    @KafkaListener(topics = "${visphere.kafka.topic.check-ota}")
    void checkIfOtaExist(Message<CheckOtaReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, reqDto -> {
            // check, if ota is valid (exist, not expired and valid for checking purpose)

            return true;
        });
    }
}
