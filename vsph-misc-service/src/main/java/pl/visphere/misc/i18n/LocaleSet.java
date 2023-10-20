/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.misc.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements LocaleExtendableSet {
    CAPTCHA_RESPONSE_SUCCESS("vsph.i18n.captcha.res.success"),
    CAPTCHA_RESPONSE_ERROR("vsph.i18n.captcha.res.error");

    private final String holder;
}
