/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;

public class GuildProfileException {
    @Slf4j
    public static class GuildProfileNotFoundException extends AbstractRestException {
        public GuildProfileNotFoundException(Long guildId) {
            super(HttpStatus.NOT_FOUND, LibLocaleSet.GUILD_PROFILE_NOT_FOUND_EXCEPTION_MESSAGE);
            log.error("Searching guild profile for guild id: '{}' not found in database.", guildId);
        }
    }
}
