/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.participant.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record GuildParticipantDetailsResDto(
    Long id,
    String fullName,
    String username,
    LocalDate joinDate,
    LocalDate memberSinceDate,
    String profileColor,
    String profileImageUrl,
    String guildProfileImageUrl,
    Boolean isOwner,
    Boolean isLoggedUser
) {
}
