/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.ILocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LibLocaleSet implements ILocaleExtendableSet {
    MISSING_REQUEST_PARAM_EXCEPTION_MESSAGE("vsph.lib.exc.MissingServletRequestParameterException"),
    UNKNOW_SERVER_EXCEPTION_MESSAGE("vsph.lib.exc.UnknowServerException");

    private final String holder;
}
