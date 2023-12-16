/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.multimedia;

import java.util.List;

public record GuildImageByIdsResDto(
    List<GuildImageData> guildImages
) {
}
