/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import pl.visphere.lib.kafka.SyncQueueHandler;

@Configuration
@RequiredArgsConstructor
class KafkaConfig {
    @Bean
    SyncQueueHandler syncQueueHandler(
        Environment environment,
        ProducerFactory<String, Object> pf,
        ConcurrentKafkaListenerContainerFactory<String, Object> factory
    ) {
        return new SyncQueueHandler(pf, factory, environment);
    }
}
