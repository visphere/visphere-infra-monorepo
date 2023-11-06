/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractExternalServiceConfig {
    private String landingUrl;
    private String clientUrl;
    private String infraGatewayUrl;
    private String s3StaticUrl;
    private String s3Url;
}
