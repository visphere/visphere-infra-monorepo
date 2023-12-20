/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GuildOwnerOverviewResDto(
    Long id,
    String name,
    String category,
    Boolean isPrivate,
    List<GuildCategoryResDto> categories
) {
}
