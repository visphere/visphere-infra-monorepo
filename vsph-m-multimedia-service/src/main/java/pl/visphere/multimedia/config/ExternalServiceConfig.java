/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pl.visphere.lib.AbstractExternalServiceConfig;

@Configuration
@ConfigurationProperties(prefix = "visphere.external-service")
public class ExternalServiceConfig extends AbstractExternalServiceConfig {
}
