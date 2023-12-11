/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.notification.i18n.LocaleSet;

public class UserNotifException {
    @Slf4j
    public static class UserNotifNotFoundException extends AbstractRestException {
        public UserNotifNotFoundException(Long userId) {
            super(HttpStatus.NOT_FOUND, LocaleSet.USER_NOTIFS_NOT_FOUND_EXCEPTION_MESSAGE);
            log.error("Searching notification settings for user with id: '{}' not found in database.", userId);
        }
    }
}
