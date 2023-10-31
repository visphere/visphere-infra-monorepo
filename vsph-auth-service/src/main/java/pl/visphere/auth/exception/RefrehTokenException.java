/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.lib.exception.AbstractRestException;

public class RefrehTokenException {
    @Slf4j
    public static class RefreshTokenNotFoundException extends AbstractRestException {
        public RefreshTokenNotFoundException(String token) {
            super(HttpStatus.NOT_FOUND, LocaleSet.REFRESH_TOKEN_NOT_FOUND_EXCEPTION_MESSAGE);
            log.error("Searching refresh token by token: '{}' not found in database", token);
        }
    }

    @Slf4j
    public static class RefreshTokenExpiredException extends AbstractRestException {
        public RefreshTokenExpiredException(String token) {
            super(HttpStatus.FORBIDDEN, LocaleSet.REFRESH_TOKEN_EXPIRED_EXCEPTION_MESSAGE);
            log.error("Current refresh token: '{}' is expired", token);
        }
    }
}
