/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.network.user;

import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.settings.network.user.dto.RelatedValueReqDto;
import pl.visphere.settings.network.user.dto.UserRelatedSettingsResDto;

interface UserSettingsService {
    UserRelatedSettingsResDto getUserSettings(AuthUserDetails user);
    BaseMessageResDto relateLangWithUser(RelatedValueReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto relateThemeWithUser(RelatedValueReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto updatePushNotificationsSettings(boolean isEnabled, AuthUserDetails user);
    BaseMessageResDto updatePushNotificationsSoundSettings(boolean isEnabled, AuthUserDetails user);
}
