/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.oauth2.core.OAuth2Supplier;
import pl.visphere.oauth2.i18n.LocaleSet;

import java.util.Map;

public class OAuth2UserException {
    @Slf4j
    public static class OAuth2UserNotFoundException extends AbstractRestException {
        public OAuth2UserNotFoundException(String oauth2UserId, OAuth2Supplier supplier) {
            super(HttpStatus.NOT_FOUND, LibLocaleSet.OAUTH2_USER_WITH_SUPPLIER_NOT_FOUND_EXCEPTION_MESSAGE, Map.of(
                "supplier", supplier
            ));
            log.error("Searched user with OAuth2 ID: '{}' and supplier: '{}' not found in database",
                oauth2UserId, supplier);
        }

        public OAuth2UserNotFoundException(Long userId) {
            super(HttpStatus.NOT_FOUND, LibLocaleSet.OAUTH2_USER_NOT_FOUND_EXCEPTION_MESSAGE);
            log.error("Searched user with OAuth2 ID: '{}' not found in database", userId);
        }
    }

    @Slf4j
    public static class OAuth2UserAlreadyActivatedException extends AbstractRestException {
        public OAuth2UserAlreadyActivatedException(String oauth2UserId, OAuth2Supplier supplier) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.OAUTH2_USER_ALREADY_ACTIVATED_EXCEPTION_MESSAGE, Map.of(
                "supplier", supplier
            ));
            log.error("Searched user account with OAuth2 ID: '{}' and supplier: '{}' has been already activated",
                oauth2UserId, supplier);
        }
    }
}
