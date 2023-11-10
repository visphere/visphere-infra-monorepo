/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import pl.visphere.auth.domain.user.UserEntity;

@Builder
public record LoginResDto(
    String fullName,
    String username,
    String emailAddress,
    String profileUrl,
    String accessToken,
    String refreshToken,
    boolean isActivated,
    boolean isMfaEnabled,
    boolean isMfaSetup
) {
    public LoginResDto(String profileImagePath, UserEntity user, String token, String refreshToken) {
        this(
            user.getFirstName() + StringUtils.SPACE + user.getLastName(),
            user.getUsername(),
            user.getEmailAddress(),
            profileImagePath,
            token,
            refreshToken,
            user.getIsActivated(),
            user.getEnabledMfa(),
            user.getMfaIsSetup()
        );
    }
}