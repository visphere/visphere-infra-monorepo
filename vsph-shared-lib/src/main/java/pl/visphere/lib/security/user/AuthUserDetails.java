/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.security.user;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.visphere.lib.kafka.payload.auth.CheckUserResDto;

import java.util.Collection;
import java.util.Set;

@Builder
@RequiredArgsConstructor
public class AuthUserDetails implements UserDetails {
    @Getter
    private final long id;
    private final String username;
    private final String password;
    @Getter
    private final String emailAddress;
    private final Set<AppGrantedAuthority> authorities;
    @Getter
    private final boolean isDisabled;

    public AuthUserDetails(CheckUserResDto resDto) {
        this.id = resDto.getId();
        this.username = resDto.getUsername();
        this.password = resDto.getPassword();
        this.emailAddress = resDto.getEmailAddress();
        this.authorities = resDto.getAuthorities();
        this.isDisabled = resDto.isDisabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
