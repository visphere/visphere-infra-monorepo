/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class UserPrincipalAuthenticationToken extends AbstractAuthenticationToken {
    private final UserDetails userDetails;

    public UserPrincipalAuthenticationToken(HttpServletRequest req, UserDetails userDetails) {
        super(userDetails.getAuthorities());
        this.userDetails = userDetails;
        setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
}
