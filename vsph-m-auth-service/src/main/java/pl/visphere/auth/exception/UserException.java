/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;

import java.util.Map;

public class UserException {
    @Slf4j
    public static class UserNotExistException extends AbstractRestException {
        public UserNotExistException(Long userId) {
            super(HttpStatus.NOT_FOUND, LibLocaleSet.USER_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE);
            log.error("Searching user by id: '{}' not found in database", userId);
        }

        public UserNotExistException(String username) {
            super(HttpStatus.NOT_FOUND, LibLocaleSet.USER_BY_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE, Map.of(
                "username", username
            ));
            log.error("Searching user by username: '{}' not found in database", username);
        }
    }

    @Slf4j
    public static class UserNotExistOrNotActivatedException extends AbstractRestException {
        public UserNotExistOrNotActivatedException(Long userId) {
            super(HttpStatus.NOT_FOUND, LibLocaleSet.ACTIVATED_USER_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE);
            log.error("Searching activated user by id: '{}' not found in database", userId);
        }
    }

    @Slf4j
    public static class UserAlreadyExistException extends AbstractRestException {
        public UserAlreadyExistException(String username, String emailAddress) {
            super(HttpStatus.BAD_REQUEST, LibLocaleSet.USER_ALREADY_EXIST_EXCEPTION_MESSAGE);
            log.error("Attempt to create user with same username: '{}' or email address: '{}'", username, emailAddress);
        }
    }

    @Slf4j
    public static class UserAlreadyActivatedException extends AbstractRestException {
        public UserAlreadyActivatedException(UserEntity user) {
            super(HttpStatus.BAD_REQUEST, LibLocaleSet.USER_ALREADY_ACTIVATED_EXCEPTION_MESSAGE);
            log.error("Attempt to already activated user: '{}'", user);
        }
    }
}
