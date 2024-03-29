/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.visphere.lib.kafka.payload.settings.UserSettingsResDto;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LoginResDto {
    private Long id;
    private String fullName;
    private String username;
    private String emailAddress;
    private String profileUrl;
    private String profileColor;
    private String accessToken;
    private String refreshToken;
    private String credentialsSupplier;
    boolean imageFromExternalProvider;
    private Boolean isDisabled;
    private LocalDate joinDate;
    private UserSettingsResDto settings;
}
