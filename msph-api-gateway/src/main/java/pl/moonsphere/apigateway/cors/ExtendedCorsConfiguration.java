/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.apigateway.cors;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
class ExtendedCorsConfiguration {
    private final CorsProperties corsProperties;

    @Bean
    CorsWebFilter corsWebFilter() {
        return new CorsPolicyBuilder(corsProperties.getAllowedOrigins())
            .addPolicy("/api/v1/misc/captcha/**", List.of(HttpMethod.POST))
            .createFilter();
    }
}
