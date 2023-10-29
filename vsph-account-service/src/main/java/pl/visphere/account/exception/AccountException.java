/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;

import java.util.Map;

public class AccountException {
    @Slf4j
    public static class AccountNotExistException extends AbstractRestException {
        public AccountNotExistException(Long userId) {
            super(HttpStatus.NOT_FOUND, LibLocaleSet.USER_ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE, Map.of("userId", userId));
            log.error("Searching account by user id: '{}' not found in database", userId);
        }
    }
}
