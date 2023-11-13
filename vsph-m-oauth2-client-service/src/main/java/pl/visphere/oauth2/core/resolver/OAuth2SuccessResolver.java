/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.oauth2.cookie.CookiesService;
import pl.visphere.oauth2.core.OAuth2Properties;
import pl.visphere.oauth2.core.repository.OAuth2Cookie;
import pl.visphere.oauth2.core.user.OAuth2UserData;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static pl.visphere.oauth2.core.repository.OAuth2Cookie.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessResolver extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2Properties oAuth2Properties;
    private final JwtService jwtService;
    private final CookiesService cookiesService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
        throws IOException {

        final String targetUrl = determineTargetUrl(req, res, auth);
        if (res.isCommitted()) {
            log.info("Response has been already committed. Unable to redirect to address: '{}'", targetUrl);
            return;
        }
        clearAuthenticationAttributes(req);
        cookiesService.deleteCookies(req, res,
            List.of(SESSION_PERSISTOR, AFTER_LOGIN_REDIR_URL, AFTER_SIGNUP_REDIR_URL));

        log.info("Redirect to '{}'", targetUrl);
        getRedirectStrategy().sendRedirect(req, res, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
        final OAuth2UserData user = (OAuth2UserData) auth.getPrincipal();
        final AuthUserDetails userDetails = user.getUserDetails();

        final String supplierName = user.getSupplier().getSupplierName();
        final TokenData tokenData = jwtService
            .generateOAuth2TemporaryToken(user.getOpenId(), userDetails.getId(), user.getSupplier().toString());

        OAuth2Cookie responseCookie = AFTER_SIGNUP_REDIR_URL;
        if (user.isAlreadySignup()) {
            responseCookie = AFTER_LOGIN_REDIR_URL;
        }
        final String redirectUrl = cookiesService.getCookieValueOrDefault(req, responseCookie, getDefaultTargetUrl());

        final List<String> authorizedBaseUris = oAuth2Properties.getAuthorizedClientRedirectUris();
        final URI redirectClientUri = URI.create(redirectUrl);

        final boolean isNotAuthorizedUri = authorizedBaseUris.stream().noneMatch(uri -> {
            final URI authorizedUri = URI.create(uri);
            return authorizedUri.getHost().equalsIgnoreCase(redirectClientUri.getHost())
                && authorizedUri.getPort() == redirectClientUri.getPort();
        });
        if (isNotAuthorizedUri) {
            log.error("Attempt to authenticate via OAuth2 by not authorized URI/s: '{}'", redirectClientUri);
            throw new GenericRestException();
        }

        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUriString(redirectUrl)
            .queryParam("token", tokenData.token())
            .queryParam("supplier", supplierName);

        log.info("Successfully login user: '{}' via OAuth2 supplier: '{}'", userDetails, supplierName);
        return uriComponentsBuilder.toUriString();
    }
}
