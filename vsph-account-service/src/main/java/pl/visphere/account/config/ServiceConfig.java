/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.visphere.lib.AbstractBaseServiceBeans;
import pl.visphere.lib.s3.S3Helper;

@Configuration
class ServiceConfig extends AbstractBaseServiceBeans {
    @Bean
    S3Helper s3Helper(Environment environment) {
        return new S3Helper(environment);
    }
}
