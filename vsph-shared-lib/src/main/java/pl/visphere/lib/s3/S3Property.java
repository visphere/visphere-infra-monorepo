/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import pl.visphere.lib.GenericProperty;

@RequiredArgsConstructor
enum S3Property implements GenericProperty {
    URL("url"),
    CDN_BASE_URL("cdn-base-url"),
    ACCESS_KEY("access-key"),
    SECRET_KEY("secret-key"),
    REGION("region"),
    PATH_STYLE_ACCESS_ENABLED("path-style-access-enabled"),
    ;

    private final String value;

    @Override
    public String getValue(Environment environment) {
        return environment.getProperty("visphere.s3." + value);
    }

    @Override
    public <T> T getValue(Environment environment, Class<T> type) {
        return environment.getProperty("visphere.s3." + value, type);
    }
}
