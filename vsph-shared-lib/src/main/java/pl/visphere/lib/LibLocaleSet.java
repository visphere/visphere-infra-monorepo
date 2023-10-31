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
    SECURITY_AUTHENTICATION_EXCEPTION_MESSAGE("vsph.lib.exc.AuthenticationException"),
    ROLE_NOT_FOUND_EXCEPTION_MESSAGE("vsph.lib.exc.roleNotFound"),
    USER_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE("vsph.lib.exc.userByIdNotFound"),
    USER_BY_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE("vsph.lib.exc.userByUsernameNotFound"),
    USER_ALREADY_EXIST_EXCEPTION_MESSAGE("vsph.lib.exc.userAlreadyExist"),
    USER_ALREADY_ACTIVATED_EXCEPTION_MESSAGE("vsph.lib.exc.userAlreadyActivated"),
    UNABLE_TO_CALL_EXTERNAL_SERVICE_EXCEPTION_MESSAGE("vsph.lib.exc.unableToCallExternalService"),
    OTA_TOKEN_NOT_FOUND_EXCEPTION_MESSAGE("vsph.lib.exc.otaTokenNotFound");

    private final String holder;
}
