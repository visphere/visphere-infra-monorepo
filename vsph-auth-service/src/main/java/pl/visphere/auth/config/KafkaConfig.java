/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import pl.visphere.lib.kafka.AbstractKafkaConfigBeans;

@Configuration
@RequiredArgsConstructor
class KafkaConfig extends AbstractKafkaConfigBeans {
}
