/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.identity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.network.identity.dto.LoginResDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;

@Component
class IdentityMapper {
    LoginResDto mapToLoginResDto(
        ProfileImageDetailsResDto profileImageDetails,
        UserEntity user,
        String token,
        String refreshToken
    ) {
        return LoginResDto.builder()
            .fullName(user.getFirstName() + StringUtils.SPACE + user.getLastName())
            .username(user.getUsername())
            .emailAddress(user.getEmailAddress())
            .profileUrl(profileImageDetails.profileImagePath())
            .accessToken(token)
            .refreshToken(refreshToken)
            .isActivated(user.getActivated())
            .isMfaEnabled(user.getEnabledMfa())
            .build();
    }
}
