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
import pl.visphere.lib.kafka.payload.notification.PersistUserNotifSettingsReqDto;
import pl.visphere.lib.kafka.sync.SyncListenerHandler;
import pl.visphere.notification.service.usernotif.UserNotifService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final UserNotifService userNotifService;

    @KafkaListener(topics = "${visphere.kafka.topic.persist-notif-user-settings}")
    void sendForActivateAccountListener(Message<PersistUserNotifSettingsReqDto> reqDto) {
        syncListenerHandler.parseAndSendResponse(reqDto, userNotifService::persistUserNotifSettings);
    }
}
