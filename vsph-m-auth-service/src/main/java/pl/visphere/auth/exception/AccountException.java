/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;

public class AccountException {
    @Slf4j
    public static class ImmutableValueException extends AbstractRestException {
        public ImmutableValueException(String action) {
            super(HttpStatus.BAD_REQUEST, LibLocaleSet.OAUTH2_IMMUTABLE_VALUE_EXCEPTION_MESSAGE);
            log.error("Attempt to change immutable value on action: '{}' for account with external credentials provider.",
                action);
        }
    }

    @Slf4j
    public static class IncorrectOldPasswordException extends AbstractRestException {
        public IncorrectOldPasswordException(String username) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.INVALID_OLD_PASSWORD_EXCEPTION_MESSAGE);
            log.error("Attempt to change password with incorrect old password for user: '{}'.", username);
        }
    }

    @Slf4j
    public static class AccountAlreadyEnabledException extends AbstractRestException {
        public AccountAlreadyEnabledException(String username) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.ACCOUNT_ALREADY_ENABLED_EXCEPTION_MESSAGE);
            log.error("Attempt to enable already enabled account for user: '{}'.", username);
        }
    }

    @Slf4j
    public static class AccountAlreadyDisabledException extends AbstractRestException {
        public AccountAlreadyDisabledException(String username) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.ACCOUNT_ALREADY_DISABLED_EXCEPTION_MESSAGE);
            log.error("Attempt to disable already disabled account for user: '{}'.", username);
        }
    }
}
