/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.kafka.payload.NullableObjectWrapper;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
public class SyncQueueHandler {
    public static final String REPLY_TOPIC_SUFFIX = "-reply-";

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
        this.replyingKafkaTemplate = fabricateTemplate(environment);
        this.replyingKafkaTemplate.start();
    }

    public <R> Optional<NullableObjectWrapper<R>> sendWithBlockThread(QueueTopic topic, Object data, Class<R> returnClazz) {
        try {
            final String decodedTopic = topic.getValue(environment);
            final String replyTopic = decodedTopic + REPLY_TOPIC_SUFFIX + getReplyHash(environment);
            final String key = UUID.randomUUID().toString();

            log.info("Started sync kafka call into {} with key: {} and data: {}", decodedTopic, key, data);

            final ProducerRecord<String, Object> record = new ProducerRecord<>(decodedTopic, null, key, data);
            record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes()));

            final RequestReplyFuture<String, Object, Object> future = replyingKafkaTemplate.sendAndReceive(record);
            final ConsumerRecord<String, Object> response = future.get();
            final KafkaNullableResponseWrapper resp = (KafkaNullableResponseWrapper) response.value();

            log.info("End sync kafka call into {} with response: {}", decodedTopic, resp);

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
        } catch (ExecutionException | InterruptedException ex) {
            log.error("Unexpected issue during sync call. Cause {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private ReplyingKafkaTemplate<String, Object, Object> fabricateTemplate(Environment environment) {
        final String groupId = environment.getProperty("visphere.kafka.group-id", "default-group");
        final String[] replyTopics = QueueTopic.getAllReplyTopics(environment);
        final ConcurrentMessageListenerContainer<String, Object> replyContainer = factory.createContainer(replyTopics);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(groupId);
        log.info("Fabricated reply templates for {}: {}", groupId, replyTopics);
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    static String getReplyHash(Environment environment) {
        return environment.getProperty("visphere.micro.instance.hash", RandomStringUtils.randomAlphanumeric(8));
    }
}
