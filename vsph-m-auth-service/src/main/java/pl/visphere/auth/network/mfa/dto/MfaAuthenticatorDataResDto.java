/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.mfa.dto;

import lombok.Builder;

@Builder
public record MfaAuthenticatorDataResDto(
    String imageUri,
    String secret
) {
}
