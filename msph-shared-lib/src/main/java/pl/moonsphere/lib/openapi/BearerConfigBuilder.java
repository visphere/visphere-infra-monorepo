/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

public class BearerConfigBuilder {
    private final String serviceTitle;
    private final String serviceVersion;
    private final Server apiGatewayServer;

    public BearerConfigBuilder(AbstractBaseProperties properties) {
        this.serviceTitle = properties.getTitle();
        this.serviceVersion = properties.getVersion();
        this.apiGatewayServer = new Server().url(properties.getUrl());
    }

    public OpenAPI build() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
            .servers(List.of(apiGatewayServer))
            .components(new Components().addSecuritySchemes(securitySchemeName, bearerScheme()))
            .security(List.of(new SecurityRequirement().addList(securitySchemeName)))
            .info(new Info().title(serviceTitle).version(serviceVersion));
    }

    private SecurityScheme bearerScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");
    }
}
