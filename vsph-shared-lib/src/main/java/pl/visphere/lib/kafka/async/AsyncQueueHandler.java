/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import pl.visphere.lib.kafka.QueueTopic;

import java.util.Locale;
import java.util.UUID;

import static pl.visphere.lib.kafka.sync.SyncQueueHandler.LOCALE_HEADER;

@Slf4j
@RequiredArgsConstructor
public class AsyncQueueHandler {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Environment environment;

    public void sendAsyncWithNonBlockingThread(QueueTopic topic, Object data) {
        final String key = UUID.randomUUID().toString();
        final String decodedTopic = topic.getValue(environment);
        final Locale currentLocale = LocaleContextHolder.getLocale();

        log.debug("Started async kafka call into '{}' with key: '{}' and data: '{}'.", decodedTopic, key, data);

        final ProducerRecord<String, Object> record = new ProducerRecord<>(decodedTopic, key, data);
        record.headers().add(LOCALE_HEADER, SerializationUtils.serialize(currentLocale));

        kafkaTemplate.send(record);
        log.debug("End async kafka call into '{}'.", decodedTopic);
    }
}
