/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.lib.i18n.LocaleExtendableSet;

public class JwtException {
    @Slf4j
    public static class JwtGeneralException extends AbstractRestException {
        public JwtGeneralException(LocaleExtendableSet placeholder) {
            super(HttpStatus.UNAUTHORIZED, placeholder);
        }

        public JwtGeneralException() {
            super(HttpStatus.UNAUTHORIZED, LibLocaleSet.JWT_INVALID_EXCEPTION_MESSAGE);
        }
    }
}
