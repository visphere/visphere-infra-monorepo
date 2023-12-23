/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.apigateway.route;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
class RouteLocatorConfig {
    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(route -> createOpenApiRoute(route, "vsph-m-user-service"))
            .route(route -> createOpenApiRoute(route, "vsph-m-misc-service"))
            .route(route -> createOpenApiRoute(route, "vsph-m-multimedia-service"))
            .route(route -> createOpenApiRoute(route, "vsph-m-notification-service"))
            .route(route -> createOpenApiRoute(route, "vsph-m-oauth2-client-service"))
            .route(route -> createOpenApiRoute(route, "vsph-m-settings-service"))
            .route(route -> createOpenApiRoute(route, "vsph-m-sphere-service"))
            .build();
    }

    private Buildable<Route> createOpenApiRoute(PredicateSpec spec, String serviceName) {
        return spec
            .path(String.format("/%s/v3/api-docs", serviceName))
            .and()
            .method(HttpMethod.GET)
            .uri(String.format("lb://%s", serviceName));
    }
}
