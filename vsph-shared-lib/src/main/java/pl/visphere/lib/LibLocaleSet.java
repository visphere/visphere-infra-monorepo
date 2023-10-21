/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LibLocaleSet implements LocaleExtendableSet {
    MISSING_REQUEST_PARAM_EXCEPTION_MESSAGE("vsph.lib.exc.MissingServletRequestParameterException"),
    UNKNOW_SERVER_EXCEPTION_MESSAGE("vsph.lib.exc.UnknowServerException"),
    JWT_INVALID_EXCEPTION_MESSAGE("vsph.lib.exc.JwtIsInvalid"),
    JWT_EXPIRED_EXCEPTION_MESSAGE("vsph.lib.exc.JwtIsExpired"),
    SECURITY_AUTHENTICATION_EXCEPTION_MESSAGE("vsph.lib.exc.AuthenticationException");

    private final String holder;
}
