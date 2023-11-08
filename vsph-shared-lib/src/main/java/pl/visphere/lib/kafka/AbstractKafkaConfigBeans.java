/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import pl.visphere.lib.kafka.async.AsyncListenerHandler;
import pl.visphere.lib.kafka.async.AsyncQueueHandler;
import pl.visphere.lib.kafka.sync.SyncListenerHandler;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;

public abstract class AbstractKafkaConfigBeans {
    @Bean
    SyncQueueHandler syncQueueHandler(
        Environment environment,
        ProducerFactory<String, Object> pf,
        ConcurrentKafkaListenerContainerFactory<String, Object> factory
    ) {
        return new SyncQueueHandler(pf, factory, environment);
    }

    @Bean
    AsyncQueueHandler asyncQueueHandler(
        Environment environment,
        KafkaTemplate<String, Object> kafkaTemplate
    ) {
        return new AsyncQueueHandler(kafkaTemplate, environment);
    }

    @Bean
    SyncListenerHandler syncListenerHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        return new SyncListenerHandler(kafkaTemplate);
    }
    
    @Bean
    AsyncListenerHandler asyncListenerHandler() {
        return new AsyncListenerHandler();
    }
}
