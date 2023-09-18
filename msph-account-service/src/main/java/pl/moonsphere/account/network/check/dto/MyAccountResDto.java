/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.check.dto;

import lombok.Builder;

@Builder
public record MyAccountResDto(
    String accountId,
    String usernameOrEmailAddress,
    String thumbnailUrl,
    boolean isVerified
) {
}
