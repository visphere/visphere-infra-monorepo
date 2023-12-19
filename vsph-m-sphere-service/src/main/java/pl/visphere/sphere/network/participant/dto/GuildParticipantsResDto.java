/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.participant.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GuildParticipantsResDto(
    Long guildId,
    GuildParticipant owner,
    List<GuildParticipant> members
) {
}
