/*
 * Copyright (c) 2024 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.chat.i18n.LocaleSet;
import pl.visphere.lib.exception.AbstractRestException;

public class MessageException {
    @Slf4j
    public static class MessageNotFoundException extends AbstractRestException {
        public MessageNotFoundException(String messageId, Long userId) {
            super(HttpStatus.NOT_FOUND, LocaleSet.MESSAGE_NOT_FOUND_EXCEPTION_MESSAGE);
            log.error("Attempt remove non existing or has insufficient permissions with ID: '{}' for user: '{}'.",
                messageId, userId);
        }
    }
}
