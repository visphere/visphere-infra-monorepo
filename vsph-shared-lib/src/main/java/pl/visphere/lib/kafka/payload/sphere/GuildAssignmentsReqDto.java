/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.sphere;

import lombok.Builder;

@Builder
public record GuildAssignmentsReqDto(
    Long userId,
    Long guildId,
    boolean throwingError
) {
}
