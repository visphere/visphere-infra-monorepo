/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginOAuth2UserDetailsResDto {
    private String username;
    private String emailAddress;
    private String fullName;
    private String accessToken;
    private String refreshToken;
    private LocalDate joinDate;
    private boolean isDisabled;
}
