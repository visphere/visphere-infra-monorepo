/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.apigateway.cors;

import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return this;
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
