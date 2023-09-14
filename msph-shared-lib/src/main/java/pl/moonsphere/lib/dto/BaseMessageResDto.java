/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib.dto;

import lombok.Builder;

@Builder
public record BaseMessageResDto(
    String message
) {
}
