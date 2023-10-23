/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum JwtState {
    VALID(null),
    INVALID(LibLocaleSet.JWT_INVALID_EXCEPTION_MESSAGE),
    EXPIRED(LibLocaleSet.JWT_EXPIRED_EXCEPTION_MESSAGE);

    private final LocaleExtendableSet placeholder;
}
