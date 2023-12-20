/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.sphere;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record GuildDetailsResDto(
    String name,
    Long ownerId,
    LocalDate createdDate
) {
}
