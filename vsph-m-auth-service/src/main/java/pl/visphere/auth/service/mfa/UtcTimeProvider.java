/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.service.mfa;

import dev.samstevens.totp.exceptions.TimeProviderException;
import dev.samstevens.totp.time.TimeProvider;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class UtcTimeProvider implements TimeProvider {
    @Override
    public long getTime() throws TimeProviderException {
        return ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond();
    }
}
