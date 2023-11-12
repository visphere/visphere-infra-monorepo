/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.user;

import lombok.Builder;
import pl.visphere.lib.security.user.AuthUserDetails;

@Builder
public record OAuth2UserResponse(
    AuthUserDetails userDetails,
    boolean isAlreadySignup
) {
}
