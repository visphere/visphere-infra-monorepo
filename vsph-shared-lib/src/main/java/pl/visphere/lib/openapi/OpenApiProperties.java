/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.openapi;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import pl.visphere.lib.Property;

@RequiredArgsConstructor
public enum OpenApiProperties implements Property {
    TITLE("title"),
    VERSION("version"),
    URL("url");

    private final String key;

    @Override
    public String getValue(Environment environment) {
        return environment.getProperty("visphere.openapi.service" + key);
    }
}
