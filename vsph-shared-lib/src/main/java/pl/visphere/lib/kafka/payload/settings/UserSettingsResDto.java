/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.settings;

import lombok.Data;

@Data
public class UserSettingsResDto {
    private String theme;
    private String lang;
    private boolean pushNotifsEnabled;
    private boolean pushNotifsSoundEnabled;

    public UserSettingsResDto() {
        pushNotifsEnabled = true;
        pushNotifsSoundEnabled = true;
    }
}
