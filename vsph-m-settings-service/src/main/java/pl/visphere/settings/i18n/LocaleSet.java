/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements LocaleExtendableSet {
    REMOVE_RELATED_LANG_RESPONSE_SUCCESS("vsph.i18n.removeRelatedLang.res.success"),
    REMOVE_RELATED_THEME_RESPONSE_SUCCESS("vsph.i18n.removeRelatedTheme.res.success"),
    PERSISTED_RELATED_LANG_RESPONSE_SUCCESS("vsph.i18n.persistRelatedLang.res.success"),
    PERSISTED_RELATED_THEME_RESPONSE_SUCCESS("vsph.i18n.persistRelatedTheme.res.success"),
    PUSH_NOTIFICATIONS_RESPONSE_SUCCESS("vsph.i18n.pushNotifications.res.success"),
    PUSH_NOTIFICATIONS_SOUND_RESPONSE_SUCCESS("vsph.i18n.pushNotificationsSound.res.success"),
    ;

    private final String holder;
}
