/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.sphere.i18n.LocaleSet;

public class BannedUserException {
    @Slf4j
    public static class UserIsNotBannedException extends AbstractRestException {
        public UserIsNotBannedException(Long userId, Long guildId) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.USER_IS_NOT_BANNED_EXCEPTION_MESSAGE);
            log.error("Attempt to unban not banned user on Sphere guild with ID: '{}' user with ID: '{}'.",
                guildId, userId);
        }
    }
}
