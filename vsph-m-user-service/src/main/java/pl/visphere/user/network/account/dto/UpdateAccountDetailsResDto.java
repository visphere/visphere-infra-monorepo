/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.account.dto;

import lombok.Builder;

@Builder
public record UpdateAccountDetailsResDto(
    String message,
    String accessToken,
    String profileImagePath
) {
}
