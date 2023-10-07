/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.openapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractBaseProperties {
    private String title;
    private String version;
    private String url;
}
