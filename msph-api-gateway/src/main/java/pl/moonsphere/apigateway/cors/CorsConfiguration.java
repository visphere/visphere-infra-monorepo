/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.apigateway.cors;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.List;

@RefreshScope
@Configuration
@RequiredArgsConstructor
class CorsConfiguration {
    private final CorsProperties corsProperties;

    @Bean
    CorsWebFilter corsWebFilter() {
        return new CorsPolicyBuilder(corsProperties.getAllowedOrigins())
            .addPolicy("/api/v1/account/new/**", List.of(HttpMethod.POST, HttpMethod.PATCH))
            .addPolicy("/api/v1/account/check/**", List.of(HttpMethod.GET, HttpMethod.PATCH))
            .addPolicy("/api/v1/auth/access/**", List.of(HttpMethod.POST, HttpMethod.PATCH, HttpMethod.DELETE))
            .addPolicy("/api/v1/auth/password/**", List.of(HttpMethod.POST, HttpMethod.PATCH, HttpMethod.GET))
            .addPolicy("/api/v1/misc/captcha/**", List.of(HttpMethod.POST))
            .createFilter();
    }
}
