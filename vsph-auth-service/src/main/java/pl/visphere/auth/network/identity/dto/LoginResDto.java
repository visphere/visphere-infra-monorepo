/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.identity.dto;

import lombok.Builder;

@Builder
public record LoginResDto(
    String fullName,
    String username,
    String emailAddress,
    String profileUrl,
    String accessToken,
    boolean isActivated
) {
}
