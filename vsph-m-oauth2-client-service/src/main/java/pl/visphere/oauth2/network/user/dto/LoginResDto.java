/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResDto {
    private String fullName;
    private String username;
    private String emailAddress;
    private String profileUrl;
    private String accessToken;
    private String refreshToken;
}
