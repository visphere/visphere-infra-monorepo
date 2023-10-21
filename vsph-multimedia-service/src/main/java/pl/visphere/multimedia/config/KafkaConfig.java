/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import pl.visphere.lib.kafka.SyncListenerHandler;
import pl.visphere.lib.kafka.SyncQueueHandler;

@Configuration
class KafkaConfig {
    @Bean
    SyncQueueHandler syncQueueHandler(
        Environment environment,
        ProducerFactory<String, Object> pf,
        ConcurrentKafkaListenerContainerFactory<String, Object> factory
    ) {
        return new SyncQueueHandler(pf, factory, environment);
    }

    @Bean
    SyncListenerHandler syncListenerHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        return new SyncListenerHandler(kafkaTemplate);
    }
}
