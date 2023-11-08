/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.identity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.network.identity.dto.LoginResDto;

@Component
class IdentityMapper {
    LoginResDto mapToLoginResDto(
        String profileImagePath,
        UserEntity user,
        String token,
        String refreshToken
    ) {
        return LoginResDto.builder()
            .fullName(user.getFirstName() + StringUtils.SPACE + user.getLastName())
            .username(user.getUsername())
            .emailAddress(user.getEmailAddress())
            .profileUrl(profileImagePath)
            .accessToken(token)
            .refreshToken(refreshToken)
            .isActivated(user.getActivated())
            .isMfaEnabled(user.getEnabledMfa())
            .build();
    }
}
