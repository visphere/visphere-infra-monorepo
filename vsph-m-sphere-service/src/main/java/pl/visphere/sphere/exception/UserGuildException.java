/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.sphere.i18n.LocaleSet;

public class UserGuildException {
    @Slf4j
    public static class UserIsNotGuildParticipantException extends AbstractRestException {
        public UserIsNotGuildParticipantException(Long userId, Long guildId) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.USER_IS_NOT_GUILD_PARTICIPANT_EXCEPTION_MESSAGE);
            log.error("Attempt to make action on non-member Sphere guild with ID: '{}' user with ID: '{}'.",
                guildId, userId);
        }
    }

    @Slf4j
    public static class DeleteGuildOwnerException extends AbstractRestException {
        public DeleteGuildOwnerException(Long guildId, Long ownerId) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.DELETE_GUILD_OWNER_EXCEPTION_MESSAGE);
            log.error("Attempt to delete guild owner with guid ID: '{}' and owner ID: '{}'.",
                guildId, ownerId);
        }
    }
}
