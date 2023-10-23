/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.identity;

import org.springframework.stereotype.Component;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.network.identity.dto.LoginResDto;
import pl.visphere.lib.kafka.payload.AccountDetailsResDto;

@Component
class IdentityMapper {
    LoginResDto mapToLoginResDto(AccountDetailsResDto accountDetails, UserEntity user, String token) {
        return LoginResDto.builder()
            .fullName(accountDetails.fullName())
            .username(user.getUsername())
            .emailAddress(user.getEmailAddress())
            .profileUrl(accountDetails.profileUrl())
            .accessToken(token)
            .isActivated(user.getActivated())
            .isMfaEnabled(user.getEnabledMfa())
            .build();
    }
}
