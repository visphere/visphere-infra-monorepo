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
import pl.visphere.lib.kafka.payload.UserDetailsResDto;

import java.util.Collection;
import java.util.Set;

@Builder
@RequiredArgsConstructor
public class AuthUserDetails implements UserDetails {
    @Getter
    private final long id;
    private final String username;
    private final String password;
    private final Set<AppGrantedAuthority> authorities;
    private final boolean isNonLocked;
    private final boolean isEnabled;

    public AuthUserDetails(UserDetailsResDto resDto) {
        this.id = resDto.id();
        this.username = resDto.username();
        this.password = resDto.password();
        this.authorities = resDto.authorities();
        this.isNonLocked = resDto.isNonLocked();
        this.isEnabled = resDto.isEnabled();
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
        return isNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
