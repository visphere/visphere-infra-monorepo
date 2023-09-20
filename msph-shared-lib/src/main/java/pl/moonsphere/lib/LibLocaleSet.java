/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.moonsphere.lib.i18n.ILocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LibLocaleSet implements ILocaleExtendableSet {
    MISSING_REQUEST_PARAM_EXCEPTION_MESSAGE("msph.lib.exc.MissingServletRequestParameterException");

    private final String holder;
}
