/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements LocaleExtendableSet {
    GET_FILL_DATA_RESPONSE_SUCCESS("vsph.i18n.getFillData.res.success"),
    UPDATE_PROFILE_IMAGE_RESPONSE_SUCCESS("vsph.i18n.updateProfileImage.res.success"),
    OAUTH2_USER_ALREADY_ACTIVATED_EXCEPTION_MESSAGE("vsph.oauth2.exc.oauth2UserAlreadyActivated"),
    ;

    private final String holder;
}
