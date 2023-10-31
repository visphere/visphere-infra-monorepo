/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class SyncListenerHandler {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T, R> void parseAndSendResponse(Message<T> message, Function<T, R> callback) {
        final MessageHeaders headers = message.getHeaders();
        final Object replyTopic = headers.get(KafkaHeaders.REPLY_TOPIC);
        if (replyTopic == null) {
            return;
        }
        final byte[] messageId = (byte[]) headers.get(KafkaHeaders.CORRELATION_ID);
        final String reply = new String((byte[]) replyTopic);
        final String key = (String) headers.get(KafkaHeaders.RECEIVED_KEY);

        R responseObject = null;
        String exPlaceholder = null;
        boolean exOccurred = false;
        Map<String, Object> params = Map.of();
        int status = HttpStatus.OK.value();
        try {
            responseObject = callback.apply(message.getPayload());
        } catch (AbstractRestException ex) {
            exPlaceholder = ex.getPlaceholder().getHolder();
            status = ex.getHttpStatus().value();
            params = ex.getVariables();
            exOccurred = true;
        } catch (RuntimeException ex) {
            exPlaceholder = LibLocaleSet.UNKNOW_SERVER_EXCEPTION_MESSAGE.getHolder();
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            exOccurred = true;
        }
        final KafkaNullableResponseWrapper responseWrapper = new KafkaNullableResponseWrapper(responseObject,
            exOccurred, exPlaceholder, status, params);

        final ProducerRecord<String, Object> record = new ProducerRecord<>(reply, null, key, responseWrapper);
        record.headers().add(new RecordHeader(KafkaHeaders.CORRELATION_ID, messageId));
        kafkaTemplate.send(record);
    }

    public <T> void parseAndSendResponse(Message<T> message, Consumer<T> callback) {
        parseAndSendResponse(message, data -> {
            callback.accept(data);
            return null;
        });
    }
}
