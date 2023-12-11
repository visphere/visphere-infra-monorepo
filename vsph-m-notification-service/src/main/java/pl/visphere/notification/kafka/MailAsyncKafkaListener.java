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
import pl.visphere.lib.kafka.payload.notification.SendStateEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.notification.service.mail.MailService;

@Slf4j
@Component
@RequiredArgsConstructor
class MailAsyncKafkaListener {
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

    @KafkaListener(topics = "${visphere.kafka.topic.email-updated-mfa-state}")
    void sendForUpdatedMfaStateListener(Message<SendStateEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::updatedMfaState);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-reset-mfa-state}")
    void sendForResetMfaStateListener(Message<SendBaseEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::resetMfaState);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-req-change-email}")
    void sendForReqChangeEmailListener(Message<SendTokenEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::reqChangeEmail);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-req-change-second-email}")
    void sendForReqChangeSecondEmailListener(Message<SendTokenEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::reqChangeSecondEmail);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-changed-email}")
    void sendForChangedEmailListener(Message<SendBaseEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::changedEmail);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-changed-second-email}")
    void sendForChangedSecondEmailListener(Message<SendBaseEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::changedSecondEmail);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-removed-second-email}")
    void sendForRemovedSecondEmailListener(Message<SendBaseEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::removedSecondEmail);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-enabled-account}")
    void sendForEnabledAccountListener(Message<SendBaseEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::enabledAccount);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-disabled-account}")
    void sendForDisabledAccountListener(Message<SendBaseEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::disabledAccount);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.email-deleted-account}")
    void sendForDeletedAccountListener(Message<SendBaseEmailReqDto> reqDto) {
        asyncListenerHandler.parseAndInvoke(reqDto, mailService::deletedAccount);
    }
}
