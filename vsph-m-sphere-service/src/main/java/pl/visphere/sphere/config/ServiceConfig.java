/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.visphere.lib.AbstractBaseServiceBeans;
import pl.visphere.lib.cache.CacheService;

@Configuration
class ServiceConfig extends AbstractBaseServiceBeans {
    @Bean
    CacheService cacheService(CacheManager cacheManager) {
        return new CacheService(cacheManager);
    }
}
