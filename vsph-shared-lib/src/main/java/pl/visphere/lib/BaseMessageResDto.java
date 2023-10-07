/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib;

import lombok.Builder;

@Builder
public record BaseMessageResDto(
    String message
) {
}
