/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.misc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import pl.visphere.lib.AbstractBaseServiceBeans;

@Configuration
class ServiceConfig extends AbstractBaseServiceBeans {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
