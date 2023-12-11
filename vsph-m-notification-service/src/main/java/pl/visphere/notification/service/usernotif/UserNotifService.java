/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.service.usernotif;

import pl.visphere.lib.kafka.payload.notification.PersistUserNotifSettingsReqDto;

public interface UserNotifService {
    void persistUserNotifSettings(PersistUserNotifSettingsReqDto reqDto);
}
