/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import pl.visphere.lib.openapi.BearerConfigBuilder;

@Configuration
@OpenAPIDefinition
@Profile({ "dev" })
@RequiredArgsConstructor
class OpenApiConfig {
    @Bean
    OpenAPI openAPI(Environment environment) {
        return new BearerConfigBuilder(environment).build();
    }
}
