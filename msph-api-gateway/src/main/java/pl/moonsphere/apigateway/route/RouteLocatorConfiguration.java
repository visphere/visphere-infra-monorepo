/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.apigateway.route;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
class RouteLocatorConfiguration {
    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(route -> createOpenApiRoute(route, "msph-account-service"))
            .route(route -> createOpenApiRoute(route, "msph-auth-service"))
            .route(route -> createOpenApiRoute(route, "msph-misc-service"))
            .route(route -> createOpenApiRoute(route, "msph-notification-service"))
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
