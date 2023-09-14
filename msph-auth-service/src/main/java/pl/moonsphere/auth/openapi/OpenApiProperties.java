/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.openapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import pl.moonsphere.lib.openapi.AbstractBaseProperties;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "moonsphere.openapi.service")
class OpenApiProperties extends AbstractBaseProperties {
}
