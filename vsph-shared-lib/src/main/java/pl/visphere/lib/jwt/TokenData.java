/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.jwt;

import lombok.Builder;

import java.util.Date;

@Builder
public record TokenData(
    String token,
    Date expiredAt
) {
}
