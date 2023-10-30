/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.env.Environment;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@RequiredArgsConstructor
public enum OtaToken {
    ACTIVATE_ACCOUNT("activate-account-hours", 48, ChronoUnit.HOURS),
    CHANGE_PASSWORD("change-password-minutes", 10, ChronoUnit.MINUTES);

    private final String holder;
    private final long defaultValue;
    private final ChronoUnit unit;

    public ZonedDateTime addTime(Environment environment) {
        final String timeFromProp = environment.getProperty("visphere.ota.life." + holder, String.valueOf(defaultValue));
        return ZonedDateTime.now().plus(NumberUtils.toLong(timeFromProp, defaultValue), unit);
    }
}
