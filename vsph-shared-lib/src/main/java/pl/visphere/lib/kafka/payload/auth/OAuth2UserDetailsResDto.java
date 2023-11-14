/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.auth;

import lombok.Builder;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.util.Set;

@Builder
public record OAuth2UserDetailsResDto(
    String username,
    String emailAddress,
    Set<AppGrantedAuthority> authorities,
    boolean isActivated
) {
}
