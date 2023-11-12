/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.apigateway.cors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
class CorsPolicyBuilder {
    private final Map<String, CorsConfiguration> corsConfigurationList = new HashMap<>();
    private final List<String> allowedOrigins;

    CorsPolicyBuilder(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    CorsPolicyBuilder addPolicy(String path, List<HttpMethod> methods) {
        final CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(allowedOrigins);
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(extractNamesFromHttpMethods(methods));
        corsConfig.addAllowedHeader("*");
        corsConfigurationList.put(path, corsConfig);
        log.info("Registered CORS policy for '{}', methods: '{}'", path, methods);
        return this;
    }

    CorsPolicyBuilder addPolicy(String path) {
        return addPolicy(path, Arrays.asList(HttpMethod.values()));
    }

    CorsWebFilter createFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        for (final Map.Entry<String, CorsConfiguration> config : corsConfigurationList.entrySet()) {
            source.registerCorsConfiguration(config.getKey(), config.getValue());
        }
        return new CorsWebFilter(source);
    }

    private List<String> extractNamesFromHttpMethods(List<HttpMethod> methods) {
        return methods.stream().map(HttpMethod::name).toList();
    }
}
