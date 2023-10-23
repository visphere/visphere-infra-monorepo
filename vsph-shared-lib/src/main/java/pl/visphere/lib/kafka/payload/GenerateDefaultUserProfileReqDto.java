/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload;

import lombok.Builder;

@Builder
public record GenerateDefaultUserProfileReqDto(
    Long userId,
    char[] initials
) {
}
