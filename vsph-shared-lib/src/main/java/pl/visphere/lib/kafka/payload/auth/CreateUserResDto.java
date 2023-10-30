/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.auth;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record CreateUserResDto(
    Long userId,
    String token,
    ZonedDateTime expiredAt
) {
}
