/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

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

        final R responseObject = callback.apply(message.getPayload());
        final KafkaNullableResponseWrapper responseWrapper = new KafkaNullableResponseWrapper(responseObject);

        final ProducerRecord<String, Object> record = new ProducerRecord<>(reply, null, key, responseWrapper);
        record.headers().add(new RecordHeader(KafkaHeaders.CORRELATION_ID, messageId));
        kafkaTemplate.send(record);
    }
}
