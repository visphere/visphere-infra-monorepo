/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements LocaleExtendableSet {
    USER_PROFILE_CUSTOM_IMAGE_UPDATE_RESPONSE_SUCCESS("vsph.i18n.userProfileCustomImageUpdate.res.success"),
    GUILD_PROFILE_CUSTOM_IMAGE_UPDATE_RESPONSE_SUCCESS("vsph.i18n.guildProfileCustomImageUpdate.res.success"),
    USER_PROFILE_IDENTICON_IMAGE_RESPONSE_SUCCESS("vsph.i18n.userProfileIdenticonImage.res.success"),
    USER_PROFILE_CUSTOM_IMAGE_DELETE_RESPONSE_SUCCESS("vsph.i18n.userProfileCustomImageDelete.res.success"),
    GUILD_PROFILE_CUSTOM_IMAGE_DELETE_RESPONSE_SUCCESS("vsph.i18n.guildProfileCustomImageDelete.res.success"),
    USER_PROFILE_COLOR_UPDATE_RESPONSE_SUCCESS("vsph.i18n.userProfileColorUpdate.res.success"),
    GUILD_PROFILE_COLOR_UPDATE_RESPONSE_SUCCESS("vsph.i18n.guildProfileColorUpdate.res.success"),
    COLOR_UPDATE_NOT_AVAILABLE_EXCEPTION_MESSAGE("vsph.multimedia.exc.colorUpdateNotAvailable"),
    ;

    private final String holder;
}
