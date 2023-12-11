/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.lib.kafka.payload.settings.UserSettingsResDto;

@Builder
public record LoginResDto(
    String fullName,
    String username,
    String emailAddress,
    String profileUrl,
    String profileColor,
    String accessToken,
    String refreshToken,
    boolean isDisabled,
    boolean isActivated,
    boolean isMfaEnabled,
    boolean isMfaSetup,
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
            user.getIsDisabled(),
            user.getIsActivated(),
            user.getMfaUser() != null,
            user.getMfaUser() != null ? user.getMfaUser().getMfaIsSetup() : false,
            resDto
        );
    }
}
