/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.sphere.i18n.LocaleSet;

import java.util.Map;

public class TextChannelException {
    @Slf4j
    public static class TextChannelNotFoundException extends AbstractRestException {
        public TextChannelNotFoundException(Long textChannelId) {
            super(HttpStatus.NOT_FOUND, LocaleSet.SPHERE_TEXT_CHANNEL_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE, Map.of(
                "textChannelId", textChannelId
            ));
            log.error("Searching sphere text channel for id: '{}' not found in database or user has not access.",
                textChannelId);
        }
    }

    @Slf4j
    public static class TextChannelDuplicateNameException extends AbstractRestException {
        public TextChannelDuplicateNameException(Long guildId) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.SPHERE_TEXT_CHANNEL_DUPLICATE_NAME_EXCEPTION_MESSAGE);
            log.error("Attempt to duplicate Sphere text channel name for guild with ID: '{}'.", guildId);
        }
    }
}
