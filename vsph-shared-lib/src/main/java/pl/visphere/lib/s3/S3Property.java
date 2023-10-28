/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import pl.visphere.lib.Property;

@RequiredArgsConstructor
enum S3Property implements Property {
    URL("url"),
    ACCESS_KEY("access-key"),
    SECRET_KEY("secret-key"),
    REGION("region");

    private final String value;

    @Override
    public String getValue(Environment environment) {
        return environment.getProperty("visphere.s3." + value);
    }
}
