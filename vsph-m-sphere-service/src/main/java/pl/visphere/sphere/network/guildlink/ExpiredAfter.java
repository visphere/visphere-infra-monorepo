/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guildlink;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.temporal.ChronoUnit;

@Getter
@RequiredArgsConstructor
public enum ExpiredAfter {
    MIN30(30, ChronoUnit.MINUTES),
    H1(1, ChronoUnit.HOURS),
    H6(6, ChronoUnit.HOURS),
    H12(12, ChronoUnit.HOURS),
    D1(1, ChronoUnit.DAYS),
    D7(7, ChronoUnit.DAYS),
    NEVER(0, null),
    ;
    
    private final int time;
    private final ChronoUnit unit;
}
