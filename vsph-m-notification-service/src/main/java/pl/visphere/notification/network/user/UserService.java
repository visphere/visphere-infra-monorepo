/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.network.user;

import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.notification.network.user.dto.UserNotifSettingsResDto;

interface UserService {
    UserNotifSettingsResDto getUserMailNotifsState(AuthUserDetails user);
    BaseMessageResDto toggleUserMailNotifsState(boolean isEnabled, AuthUserDetails user);
}
