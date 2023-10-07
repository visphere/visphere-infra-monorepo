/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.misc.openapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import pl.visphere.lib.openapi.AbstractBaseProperties;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "visphere.openapi.service")
class OpenApiProperties extends AbstractBaseProperties {
}
