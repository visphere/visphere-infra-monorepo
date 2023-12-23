/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AccountDetailsResDto {
    private String firstName;
    private String lastName;
    private String username;
    private String emailAddress;
    private String secondEmailAddress;
    private LocalDate birthDate;
    private boolean isExternalOAuth2Supplier;
    private boolean isMfaEnabled;
    private boolean isMfaSetup;
}
