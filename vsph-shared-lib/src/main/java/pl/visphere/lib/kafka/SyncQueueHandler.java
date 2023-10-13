/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
public class SyncQueueHandler {
    public static final String REPLY_TOPIC_SUFFIX = "-reply";

    private final ProducerFactory<String, Object> pf;
    private final ConcurrentKafkaListenerContainerFactory<String, Object> factory;
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
        this.replyingKafkaTemplate = fabricateTemplate(environment);
        this.replyingKafkaTemplate.start();
    }

    public <R> Optional<R> sendWithBlockThread(QueueTopic topic, Object data, Class<R> returnClazz) {
        try {
            final String decodedTopic = topic.getKey(environment);
            log.info("Started sync kafka call into {} with: {}", decodedTopic, data);
            final ProducerRecord<String, Object> record = new ProducerRecord<>(decodedTopic, data);
            final RequestReplyFuture<String, Object, Object> future = replyingKafkaTemplate.sendAndReceive(record);
            final ConsumerRecord<String, Object> response = future.get();
            log.info("End sync kafka call into {} with: {}", decodedTopic, data);
            return Optional.of(returnClazz.cast(response.value()));
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
}
