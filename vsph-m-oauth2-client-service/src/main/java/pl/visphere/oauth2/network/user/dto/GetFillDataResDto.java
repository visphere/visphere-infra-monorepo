/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.user.dto;

import lombok.Builder;

@Builder
public record GetFillDataResDto(
    String message,
    String firstName,
    String lastName,
    String username,
    String profileImageUrl,
    String supplier
) {
}
