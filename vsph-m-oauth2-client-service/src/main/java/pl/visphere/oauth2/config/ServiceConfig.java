/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.visphere.lib.AbstractBaseServiceBeans;
import pl.visphere.oauth2.core.user.OAuth2UserLoader;
import pl.visphere.oauth2.service.oauth2service.OAuth2ServiceImpl;

@Configuration
class ServiceConfig extends AbstractBaseServiceBeans {
    @Bean
    OAuth2UserLoader oAuth2UserLoader() {
        return new OAuth2ServiceImpl();
    }
}
