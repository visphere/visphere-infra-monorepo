/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.dto;

import lombok.Builder;

@Builder
public record MessageWithResourcePathResDto(
    String message,
    String resourcePath
) {
}
