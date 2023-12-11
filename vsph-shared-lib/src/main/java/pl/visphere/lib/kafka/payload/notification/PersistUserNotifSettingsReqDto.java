/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.notification;

import lombok.Builder;

@Builder
public record PersistUserNotifSettingsReqDto(
    Long userId,
    boolean isEmailNotifsEnabled
) {
}
