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
    USER_PROFILE_GRAVATAR_IMAGE_RESPONSE_SUCCESS("vsph.i18n.userProfileGravatarImage.res.success"),
    USER_PROFILE_CUSTOM_IMAGE_DELETE_RESPONSE_SUCCESS("vsph.i18n.userProfileCustomImageDelete.res.success");

    private final String holder;
}
