/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class AsyncQueueHandler {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Environment environment;

    public void sendAsyncWithNonBlockingThread(QueueTopic topic, Object data) {
        kafkaTemplate.send(topic.getValue(environment), data);
    }
}
