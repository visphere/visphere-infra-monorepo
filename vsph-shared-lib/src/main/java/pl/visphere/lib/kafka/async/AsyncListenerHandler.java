/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.async;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.function.Consumer;

import static pl.visphere.lib.kafka.sync.SyncQueueHandler.LOCALE_HEADER;

public class AsyncListenerHandler {
    public <T> void parseAndInvoke(Message<T> message, Consumer<T> callback) {
        final MessageHeaders headers = message.getHeaders();
        final Object localeHeader = headers.get(LOCALE_HEADER);
        if (localeHeader == null) {
            return;
        }
        LocaleContextHolder.setLocale(SerializationUtils.deserialize((byte[]) localeHeader));
        callback.accept(message.getPayload());
    }
}
