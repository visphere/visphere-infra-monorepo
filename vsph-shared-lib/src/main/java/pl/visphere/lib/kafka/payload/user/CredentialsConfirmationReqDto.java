/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.user;

import lombok.Builder;

@Builder
public record CredentialsConfirmationReqDto(
    Long userId,
    String password,
    String mfaCode
) {
}