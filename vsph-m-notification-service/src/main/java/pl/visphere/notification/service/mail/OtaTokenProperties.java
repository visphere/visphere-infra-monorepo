/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.service.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "visphere.ota.life")
class OtaTokenProperties {
    private Integer activateAccountHours;
    private Integer changePasswordMinutes;
    private Integer mfaEmailMinutes;
    private Integer changeEmailMinutes;
}
