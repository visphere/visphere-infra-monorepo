/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild.dto;

import lombok.Builder;

@Builder
public record GuildDetailsResDto(
    Long id,
    String name,
    String category,
    String profileColor,
    String profileImageUrl,
    Boolean isPrivate,
    Long ownerId,
    Boolean isLoggedUserIsOwner
) {
}
