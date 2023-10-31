/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.multimedia;

import lombok.Builder;

@Builder
public record ProfileImageDetailsResDto(
    String profileColor,
    String profileImageUuid,
    String profileImagePath
) {
}