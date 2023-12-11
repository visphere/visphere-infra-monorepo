/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.lib.exception.AbstractRestException;

public class MfaException {
    @Slf4j
    public static class MfaAlreadyIsSetupException extends AbstractRestException {
        public MfaAlreadyIsSetupException(String username) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.MFA_IS_ALREADY_SETUP_EXCEPTION_MESSAGE);
            log.error("Attempt to re-setup MFA for user: '{}'.", username);
        }
    }

    @Slf4j
    public static class MfaNotEnabledException extends AbstractRestException {
        public MfaNotEnabledException(String username) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.MFA_NOT_ENABLED_EXCEPTION_MESSAGE);
            log.error("Attempt to access MFA features for non-enabled MFA account: '{}'.", username);
        }
    }

    @Slf4j
    public static class MfaCurrentlyEnabledException extends AbstractRestException {
        public MfaCurrentlyEnabledException(String username) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.MFA_CURRENTLY_ENABLED_EXCEPTION_MESSAGE);
            log.error("Attempt to enable MFA features for already enabled on account: '{}'", username);
        }
    }

    @Slf4j
    public static class MfaCurrentlyDisabledException extends AbstractRestException {
        public MfaCurrentlyDisabledException(String username) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.MFA_CURRENTLY_DISABLED_EXCEPTION_MESSAGE);
            log.error("Attempt to disable MFA features for already disabled on account: '{}'.", username);
        }
    }

    @Slf4j
    public static class MfaInvalidCodeException extends AbstractRestException {
        public MfaInvalidCodeException(String code, String username) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.MFA_INVALID_CODE_EXCEPTION_MESSAGE);
            log.error("Attempt to authorize MFA with invalid code: '{}' for user: '{}'.", code, username);
        }

        public MfaInvalidCodeException(String code) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.MFA_INVALID_CODE_EXCEPTION_MESSAGE);
            log.error("Attempt to authorize MFA with invalid code: '{}'.", code);
        }
    }
}
