/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.network.user.dto;

import lombok.Builder;

@Builder
public record UserNotifSettingsResDto(
    Boolean isEmailNotifsEnabled
) {
}
