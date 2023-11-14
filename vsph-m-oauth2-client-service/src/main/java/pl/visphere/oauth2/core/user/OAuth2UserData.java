/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.user;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.oauth2.core.OAuth2Supplier;

import java.util.Collection;
import java.util.Map;

@Builder
@RequiredArgsConstructor
public class OAuth2UserData implements OAuth2User, OidcUser {
    @Getter
    private final AuthUserDetails userDetails;
    @Getter
    private final boolean isAlreadySignup;
    private final Map<String, Object> attributes;
    private final UserLoaderData userLoaderData;
    @Getter
    private final String openId;

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userLoaderData.oidcUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return userLoaderData.oidcIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetails.getAuthorities();
    }

    @Override
    public String getName() {
        return userDetails.getUsername();
    }

    public OAuth2Supplier getSupplier() {
        return userLoaderData.supplier();
    }
}
