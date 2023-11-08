/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import pl.visphere.lib.Property;

@Getter
@RequiredArgsConstructor
public enum CdnProperty implements Property {
    CDN_BASE_URL("base-url"),
    ;

    private final String value;

    @Override
    public String getValue(Environment environment) {
        return environment.getProperty("visphere.cdn." + value);
    }
}
