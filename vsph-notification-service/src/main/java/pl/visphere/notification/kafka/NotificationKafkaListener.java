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
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;

@Slf4j
@Component
@RequiredArgsConstructor
class NotificationKafkaListener {
    @KafkaListener(topics = "${visphere.kafka.topic.email-activate-account}")
    void sendForActivateAccount(Message<SendTokenEmailReqDto> payload) {
        // send email message
        log.info("sendForActivateAccount");
        System.out.println(payload);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-new-account}")
    void sendForNewAccount(Message<SendBaseEmailReqDto> payload) {
        // send email message
        log.info("sendForNewAccount");
        System.out.println(payload);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-change-password}")
    void sendForChangePassword(Message<SendTokenEmailReqDto> payload) {
        // send email message
        log.info("sendForChangePassword");
        System.out.println(payload);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-password-changed}")
    void sendForPasswordChanged(Message<SendBaseEmailReqDto> payload) {
        // send email message
        log.info("sendForPasswordChanged");
        System.out.println(payload);
    }
}
