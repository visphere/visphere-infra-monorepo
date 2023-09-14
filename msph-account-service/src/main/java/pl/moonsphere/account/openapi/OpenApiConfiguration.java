/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.moonsphere.lib.openapi.BearerConfigBuilder;

@Configuration
@OpenAPIDefinition
@Profile({"dev"})
@RequiredArgsConstructor
class OpenApiConfiguration {
    private final OpenApiProperties openApiProperties;

    @Bean
    OpenAPI openAPI() {
        return new BearerConfigBuilder(openApiProperties).build();
    }
}
