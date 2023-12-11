/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.visphere.lib.i18n.AppLocale;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.oauth2.cookie.CookiesService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static pl.visphere.oauth2.core.repository.OAuth2Cookie.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureResolver extends SimpleUrlAuthenticationFailureHandler {
    private final CookiesService cookiesService;
    private final I18nService i18nService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
        throws IOException {

        final String targetUrl = cookiesService.getCookieValueOrDefault(req, AFTER_LOGIN_REDIR_URL, "/");

        final Locale localeCookie = cookiesService
            .getCookie(req, LANG)
            .map(c -> cookiesService.deserializeValue(c, Locale.class))
            .orElse(AppLocale.PL);

        cookiesService.deleteCookies(req, res,
            List.of(SESSION_PERSISTOR, AFTER_LOGIN_REDIR_URL, AFTER_SIGNUP_REDIR_URL));

        LocaleContextHolder.setLocale(localeCookie);
        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUriString(targetUrl)
            .queryParam("message", i18nService.getMessage(ex.getMessage()))
            .queryParam("type", "danger");

        log.error("OAuth2 authorization failed. Redirect to: '{}'. Cause: '{}'.", targetUrl, ex.getMessage());
        getRedirectStrategy().sendRedirect(req, res, uriComponentsBuilder.toUriString());
    }
}
