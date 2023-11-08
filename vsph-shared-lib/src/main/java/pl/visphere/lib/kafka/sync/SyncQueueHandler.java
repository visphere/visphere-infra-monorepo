/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.kafka.KafkaNullableResponseWrapper;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.ResponseObject;
import pl.visphere.lib.kafka.payload.NullableObjectWrapper;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class SyncQueueHandler {
    public static final String REPLY_TOPIC_SUFFIX = "-reply-";
    public static final String LOCALE_HEADER = "locale";

    private final ProducerFactory<String, Object> pf;
    private final ConcurrentKafkaListenerContainerFactory<String, Object> factory;
    private final ObjectMapper objectMapper;
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    private final Environment environment;

    public SyncQueueHandler(
        ProducerFactory<String, Object> pf,
        ConcurrentKafkaListenerContainerFactory<String, Object> factory,
        Environment environment
    ) {
        this.pf = pf;
        this.factory = factory;
        this.environment = environment;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.replyingKafkaTemplate = fabricateTemplate(environment);
        this.replyingKafkaTemplate.start();
    }

    public <R> Optional<NullableObjectWrapper<R>> sendWithBlockThread(
        QueueTopic topic, Object data, Class<R> returnClazz
    ) {
        try {
            final String decodedTopic = topic.getValue(environment);
            final String replyTopic = decodedTopic + REPLY_TOPIC_SUFFIX + getReplyHash(environment);
            final String key = UUID.randomUUID().toString();
            final Locale currentLocale = LocaleContextHolder.getLocale();

            log.info("Started sync kafka call into '{}' with key: '{}' and data: '{}'", decodedTopic, key, data);

            final ProducerRecord<String, Object> record = new ProducerRecord<>(decodedTopic, null, key, data);
            record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes()));
            record.headers().add(new RecordHeader(LOCALE_HEADER, SerializationUtils.serialize(currentLocale)));

            final RequestReplyFuture<String, Object, Object> future = replyingKafkaTemplate.sendAndReceive(record);
            final ConsumerRecord<String, Object> response = future.get(10, TimeUnit.SECONDS);
            final KafkaNullableResponseWrapper resp = (KafkaNullableResponseWrapper) response.value();

            log.info("End sync kafka call into '{}' with response: '{}'", decodedTopic, resp);

            ResponseObject responseObject = ResponseObject.IS_NULL;
            R payload = null;
            if (resp.exOccurred()) {
                throw new GenericRestException(resp);
            }
            if (resp.payload() != null) {
                payload = objectMapper.convertValue(resp.payload(), returnClazz);
                responseObject = ResponseObject.IS_INSTANTIATED;
            }
            return Optional.of(new NullableObjectWrapper<>(responseObject, payload));
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            log.error("Unexpected issue during sync call. Cause '{}'", ex.getMessage());
            return Optional.empty();
        }
    }

    public <R> R sendNotNullWithBlockThread(QueueTopic topic, Object data, Class<R> returnClazz) {
        return sendWithBlockThread(topic, data, returnClazz)
            .map(NullableObjectWrapper::content)
            .orElseThrow(RuntimeException::new);
    }

    private ReplyingKafkaTemplate<String, Object, Object> fabricateTemplate(Environment environment) {
        final String groupId = environment.getProperty("visphere.kafka.group-id", "default-group");
        final String[] replyTopics = QueueTopic.getAllReplyTopics(environment);
        final ConcurrentMessageListenerContainer<String, Object> replyContainer = factory.createContainer(replyTopics);
        final ContainerProperties props = replyContainer.getContainerProperties();
        props.setMissingTopicsFatal(false);
        props.setGroupId(groupId);
        log.info("Fabricated reply templates for '{}': '{}'", groupId, replyTopics);
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    public static String getReplyHash(Environment environment) {
        return environment.getProperty("visphere.micro.instance.hash", RandomStringUtils.randomAlphanumeric(8));
    }
}
