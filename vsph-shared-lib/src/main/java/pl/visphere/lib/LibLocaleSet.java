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
    MISSING_REQUEST_PARAM_EXCEPTION_MESSAGE("vsph.lib.exc.missingServletRequestParameterException"),
    UNKNOW_SERVER_EXCEPTION_MESSAGE("vsph.lib.exc.unknowServerException"),
    JWT_INVALID_EXCEPTION_MESSAGE("vsph.lib.exc.jwtIsInvalid"),
    JWT_EXPIRED_EXCEPTION_MESSAGE("vsph.lib.exc.jwtIsExpired"),
    SECURITY_AUTHENTICATION_EXCEPTION_MESSAGE("vsph.lib.exc.authenticationException"),
    ROLE_NOT_FOUND_EXCEPTION_MESSAGE("vsph.lib.exc.roleNotFound"),
    USER_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE("vsph.lib.exc.userByIdNotFound"),
    USER_BY_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE("vsph.lib.exc.userByUsernameNotFound"),
    USER_ALREADY_EXIST_EXCEPTION_MESSAGE("vsph.lib.exc.userAlreadyExist"),
    USER_ALREADY_ACTIVATED_EXCEPTION_MESSAGE("vsph.lib.exc.userAlreadyActivated"),
    UNABLE_TO_CALL_EXTERNAL_SERVICE_EXCEPTION_MESSAGE("vsph.lib.exc.unableToCallExternalService"),
    OTA_TOKEN_NOT_FOUND_EXCEPTION_MESSAGE("vsph.lib.exc.otaTokenNotFound"),
    ACCOUNT_PROFILE_EXCEPTION_MESSAGE("vsph.lib.exc.accountProfileNotFound");

    private final String holder;
}
