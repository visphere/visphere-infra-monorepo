/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import pl.visphere.lib.AbstractBaseServiceBeans;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.s3.S3Client;
import pl.visphere.lib.s3.S3Helper;

@Configuration
class ServiceConfig extends AbstractBaseServiceBeans {
    @Bean
    S3Client s3Client(Environment environment) {
        final S3Client s3Client = new S3Client(environment);
        s3Client.initialize();
        return s3Client;
    }

    @Bean
    S3Helper s3Helper(Environment environment) {
        return new S3Helper(environment);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    CacheService cacheService(CacheManager cacheManager) {
        return new CacheService(cacheManager);
    }
}
