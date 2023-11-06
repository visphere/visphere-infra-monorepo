/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.hbs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pl.visphere.notification.hbs.dto.MobileAppLinksDto;
import pl.visphere.notification.hbs.dto.SocialLinksDto;

@Getter
@Setter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "visphere.hbs")
public class HbsProperties {
    private String templatesPath;
    private String layoutsPath;
    private String extension;
    private String fontName;
    private String fontResourcePath;
    private MobileAppLinksDto mobile;
    private SocialLinksDto social;
}
