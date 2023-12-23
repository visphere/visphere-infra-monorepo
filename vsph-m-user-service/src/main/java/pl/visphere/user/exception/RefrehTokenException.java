/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.user.i18n.LocaleSet;

public class RefrehTokenException {
    @Slf4j
    public static class RefreshTokenExpiredException extends AbstractRestException {
        public RefreshTokenExpiredException(String token) {
            super(HttpStatus.FORBIDDEN, LocaleSet.REFRESH_TOKEN_EXPIRED_EXCEPTION_MESSAGE);
            log.error("Current refresh token: '{}' is expired.", token);
        }
    }
}
