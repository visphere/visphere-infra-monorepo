/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.sphere.i18n.LocaleSet;

public class SphereGuildJoinException {
    @Slf4j
    public static class AlreadyIsOnSphereException extends AbstractRestException {
        public AlreadyIsOnSphereException(Long guildId, Long userId) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.ALREADY_IS_ON_SPHERE_EXCEPTION_MESSAGE);
            log.error("Attempt to join to guild Sphere by ID: '{}' by already joined user with ID: '{}'.",
                guildId, userId);
        }
    }

    @Slf4j
    public static class JoinLinkExpiredException extends AbstractRestException {
        public JoinLinkExpiredException(Long guildId, Long userId) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.SPHERE_JOIN_LINK_IS_EXPIRED_EXCEPTION_MESSAGE);
            log.error("Attempt to join to guild Sphere by ID: '{}' with expired join code by user with ID: '{}'.",
                guildId, userId);
        }
    }
}
