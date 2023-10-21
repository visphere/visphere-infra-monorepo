/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload;

import lombok.Builder;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.util.Set;

@Builder
public record UserDetailsResDto(
    long id,
    String username,
    String password,
    Set<AppGrantedAuthority> authorities,
    boolean isNonLocked,
    boolean isEnabled
) {
}
