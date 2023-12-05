/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.network.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRelatedSettingsResDto {
    private String lang;
    private String theme;
    private boolean pushNotifsEnabled;
    private boolean pushNotifsSoundEnabled;
}
