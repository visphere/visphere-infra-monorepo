/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.access.dto;

import lombok.Builder;

@Builder
public record LoginResDto(
    String fullName,
    String username,
    String emailAddress,
    String profileUrl,
    String accessToken,
    String refreshToken,
    boolean isActivated
) {
}
