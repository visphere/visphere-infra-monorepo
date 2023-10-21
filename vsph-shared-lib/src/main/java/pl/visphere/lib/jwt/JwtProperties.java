/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import pl.visphere.lib.GenericProperty;

@RequiredArgsConstructor
public enum JwtProperties implements GenericProperty {
    JWT_SECRET_KEY("secret-key"),
    JWT_ISSUER("issuer"),
    JWT_AUDIENCE("audience"),
    JWT_ACCESS_LIFE_MINUTES("access.life-minutes"),
    JWT_REFRESH_LIFE_HOURS("refresh.life-hours");

    private final String key;

    @Override
    public String getValue(Environment environment) {
        return environment.getProperty("visphere.jwt." + key);
    }

    @Override
    public <T> T getValue(Environment environment, Class<T> type) {
        return environment.getProperty("visphere.jwt." + key, type);
    }
}
