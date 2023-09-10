/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.misc.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.moonsphere.lib.i18n.ILocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements ILocaleExtendableSet {
    CAPTCHA_RESPONSE_SUCCESS("msph.i18n.captcha.res.success"),
    CAPTCHA_RESPONSE_ERROR("msph.i18n.captcha.res.error");

    private final String holder;
}
