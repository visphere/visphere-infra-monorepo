/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.joinguild.dto;

import lombok.Builder;

@Builder
public record JoinGuildResDto(
    long guildId,
    String message
) {
}
