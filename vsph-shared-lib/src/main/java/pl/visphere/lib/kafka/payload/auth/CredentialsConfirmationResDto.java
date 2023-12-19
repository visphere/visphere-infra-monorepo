/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.auth;

import lombok.Builder;

@Builder
public record CredentialsConfirmationResDto(
    Boolean credentialsAreValid
) {
}
