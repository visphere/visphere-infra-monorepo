/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.account.dto;

import lombok.Builder;

@Builder
public record ActivateAccountResDto(
    String message,
    boolean mfaEnabled
) {
}