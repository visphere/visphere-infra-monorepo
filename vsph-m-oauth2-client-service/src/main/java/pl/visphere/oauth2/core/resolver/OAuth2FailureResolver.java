/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.visphere.oauth2.cookie.CookiesService;
import pl.visphere.oauth2.core.repository.OAuth2Cookie;

import java.io.IOException;
import java.util.List;

import static pl.visphere.oauth2.core.repository.OAuth2Cookie.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureResolver extends SimpleUrlAuthenticationFailureHandler {
    private final CookiesService cookiesService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
        throws IOException {

        final String targetUrl = cookiesService.getCookieValueOrDefault(req, OAuth2Cookie.AFTER_LOGIN_REDIR_URL, "/");
        cookiesService.deleteCookies(req, res,
            List.of(SESSION_PERSISTOR, AFTER_LOGIN_REDIR_URL, AFTER_SIGNUP_REDIR_URL));

        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUriString(targetUrl)
            .queryParam("message", ex.getLocalizedMessage())
            .queryParam("error", true);

        log.error("OAuth2 authorization failed. Redirect to: '{}'. Cause: '{}'", targetUrl, ex.getMessage());
        getRedirectStrategy().sendRedirect(req, res, uriComponentsBuilder.toString());
    }
}
