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
import pl.visphere.lib.kafka.async.AsyncListenerHandler;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.notification.service.mail.MailService;

@Slf4j
@Component
@RequiredArgsConstructor
class NotificationKafkaListener {
    private final AsyncListenerHandler asyncListenerHandler;
    private final MailService mailService;

    @KafkaListener(topics = "${visphere.kafka.topic.email-activate-account}")
    void sendForActivateAccountListener(Message<SendTokenEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::activateAccount);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-new-account}")
    void sendForNewAccountListener(Message<SendBaseEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::newAccount);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-change-password}")
    void sendForChangePasswordListener(Message<SendTokenEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::changePassword);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-password-changed}")
    void sendForPasswordChangedListener(Message<SendBaseEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::passwordChanged);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-mfa-code}")
    void sendForMfaEmailCodeListener(Message<SendTokenEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::mfaCode);
    }
}
