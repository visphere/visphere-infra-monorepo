/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.multimedia;

import lombok.Builder;

import java.util.List;

@Builder
public record GuildImageByIdsReqDto(
    List<Long> guildIds
) {
}
