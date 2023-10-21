/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.SyncListenerHandler;

@Slf4j
@Component
@RequiredArgsConstructor
class NotificationKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
}
