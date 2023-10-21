/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;

public class JwtException {
    @Slf4j
    public static class JwtIsInvalidException extends AbstractRestException {
        public JwtIsInvalidException() {
            super(HttpStatus.UNAUTHORIZED, LibLocaleSet.JWT_INVALID_EXCEPTION_MESSAGE);
        }
    }

    @Slf4j
    public static class JwtIsExpiredException extends AbstractRestException {
        public JwtIsExpiredException() {
            super(HttpStatus.UNAUTHORIZED, LibLocaleSet.JWT_EXPIRED_EXCEPTION_MESSAGE);
        }
    }
}
