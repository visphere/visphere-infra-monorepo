/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.create.dto;

import lombok.Builder;

@Builder
public record ActivateAccountResDto(
    String message,
    boolean mfaEnabled
) {
}
