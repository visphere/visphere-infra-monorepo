/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild.dto;

import lombok.Builder;

@Builder
public record AddGuildResDto(
    Long id,
    String message
) {
}
