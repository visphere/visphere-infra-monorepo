/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.lib.kafka.payload.settings.UserSettingsResDto;

import java.time.LocalDate;
import java.time.ZoneId;

@Builder
public record LoginResDto(
    String fullName,
    String username,
    String emailAddress,
    String profileUrl,
    String profileColor,
    String accessToken,
    String refreshToken,
    String credentialsSupplier,
    boolean isDisabled,
    boolean isActivated,
    boolean isMfaEnabled,
    boolean isMfaSetup,
    LocalDate joinDate,
    UserSettingsResDto settings
) {
    public LoginResDto(
        String profileImagePath, String profileColor, UserEntity user, String token, String refreshToken,
        UserSettingsResDto resDto
    ) {
        this(
            user.getFirstName() + StringUtils.SPACE + user.getLastName(),
            user.getUsername(),
            user.getEmailAddress(),
            profileImagePath,
            profileColor,
            token,
            refreshToken,
            "local",
            user.getIsDisabled(),
            user.getIsActivated(),
            user.getMfaUser() != null,
            user.getMfaUser() != null ? user.getMfaUser().getMfaIsSetup() : false,
            user.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate(),
            resDto
        );
    }
}
