/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import pl.visphere.lib.security.AbstractSecurityConfigBeans;
import pl.visphere.oauth2.core.OAuth2CustomTokenConverter;

import java.util.Arrays;

@Configuration
class SecurityBeans extends AbstractSecurityConfigBeans {
    @Bean
    OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> configureOAuth2AccessToken() {
        final var converter = new OAuth2AccessTokenResponseHttpMessageConverter();
        final var responseClient = new DefaultAuthorizationCodeTokenResponseClient();
        converter.setAccessTokenResponseConverter(new OAuth2CustomTokenConverter());
        final var template = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), converter));
        template.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        responseClient.setRestOperations(template);
        return responseClient;
    }
}
