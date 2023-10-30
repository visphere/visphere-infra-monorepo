/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.service.otatoken.dto;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record GenerateOtaResDto(
    String token,
    ZonedDateTime expiredAt
) {
}
