/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.lib.security.OtaToken;

public class OtaTokenException {
    @Slf4j
    public static class OtaTokenNotFoundException extends AbstractRestException {
        public OtaTokenNotFoundException(String token, OtaToken type) {
            super(HttpStatus.NOT_FOUND, LibLocaleSet.OTA_TOKEN_NOT_FOUND_EXCEPTION_MESSAGE);
            log.error("Searching ota token by token: '{}' and type: '{}' not found in database.", token, type.getHolder());
        }
    }
}
