/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum AppGrantedAuthority implements GrantedAuthority {
    USER("USER");

    private final String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
