/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.sync.SyncListenerHandler;
import pl.visphere.settings.service.related.RelatedSettingsService;

@Slf4j
@Component
@RequiredArgsConstructor
class SettingsKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final RelatedSettingsService relatedSettingsService;

    @KafkaListener(topics = "${visphere.kafka.topic.get-user-persisted-related-settings}")
    void getPersistedUserRelatedSettingsListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, relatedSettingsService::getPersistedUserRelatedSettings);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.instantiate-user-related-settings}")
    void instantiateUserRelatedSettingsListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, relatedSettingsService::instantiateUserRelatedSettings);
    }
}
