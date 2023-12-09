/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.account.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AccountDetailsResDto(
    String firstName,
    String lastName,
    String username,
    String emailAddress,
    String secondEmailAddress,
    LocalDate birthDate,
    LocalDate joinDate,
    boolean isExternalOAuth2Supplier,
    String credentialsSupplier,
    boolean isMfaEnabled
) {
}
