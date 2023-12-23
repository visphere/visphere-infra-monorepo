/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResDto {
    private String firstName;
    private String lastName;
    private String username;
    private LocalDate joinDate;
    private boolean isActivated;
    private boolean isLocked;
    private boolean isExternalCredentialsSupplier;
}
