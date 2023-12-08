/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.apigateway.cors;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@RefreshScope
@Configuration
@RequiredArgsConstructor
class CorsConfiguration {
    private final CorsProperties corsProperties;

    @Bean
    CorsWebFilter corsWebFilter() {
        return new CorsPolicyBuilder(corsProperties.getAllowedOrigins())
            .addPolicy("/api/v1/auth/account/**", List.of(GET, POST, PATCH))
            .addPolicy("/api/v1/auth/check/**", List.of(GET, PATCH))
            .addPolicy("/api/v1/auth/email/**", List.of(POST, PATCH, DELETE))
            .addPolicy("/api/v1/auth/identity/**", List.of(POST, PATCH, DELETE))
            .addPolicy("/api/v1/auth/password/renew/**", List.of(POST, PATCH))
            .addPolicy("/api/v1/auth/mfa/**", List.of(POST, PATCH))
            .addPolicy("/api/v1/misc/captcha/**", List.of(POST))
            .addPolicy("/api/v1/multimedia/profile/color/**", List.of(GET, PATCH))
            .addPolicy("/api/v1/multimedia/profile/image/**", List.of(POST, DELETE))
            .addPolicy("/api/v1/notification/mail/mirror/**", List.of(POST))
            .addPolicy("/oauth2/**")
            .addPolicy("/api/v1/oauth2/user/**", List.of(GET, PATCH, POST))
            .addPolicy("/api/v1/settings/user/**", List.of(GET, PATCH))
            .addPolicy("/api/v1/sphere/guild/**", List.of(POST, PATCH))
            .addPolicy("/api/v1/sphere/link/**", List.of(GET, POST, PATCH, DELETE))
            .createFilter();
    }
}
