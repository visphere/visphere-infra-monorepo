/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;

public class AccountProfileException {
    @Slf4j
    public static class AccountProfileNotFoundException extends AbstractRestException {
        public AccountProfileNotFoundException(Long userId) {
            super(HttpStatus.NOT_FOUND, LibLocaleSet.ACCOUNT_PROFILE_NOT_FOUND_EXCEPTION_MESSAGE);
            log.error("Searching account profile for user id: '{}' not found in database", userId);
        }
    }
}
