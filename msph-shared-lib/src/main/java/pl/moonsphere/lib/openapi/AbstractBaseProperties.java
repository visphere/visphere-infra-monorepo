/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib.openapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractBaseProperties {
    private String title;
    private String version;
    private String url;
}
