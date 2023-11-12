/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.repository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.oauth2.cookie.ConfigurableCookie;
import pl.visphere.oauth2.cookie.CookiePayload;
import pl.visphere.oauth2.cookie.CookiesService;
import pl.visphere.oauth2.core.OAuth2Properties;

import java.util.StringJoiner;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OAuth2Repository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private final OAuth2Properties oAuth2Properties;
    private final CookiesService cookiesService;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest req) {
        return cookiesService.getCookie(req, OAuth2Cookie.SESSION_PERSISTOR)
            .map(c -> cookiesService.deserializeValue(c, OAuth2AuthorizationRequest.class))
            .orElseThrow(() -> {
                log.error("Unable to find cookie: '{}'", OAuth2Cookie.SESSION_PERSISTOR);
                return new GenericRestException();
            });
    }

    @Override
    public void saveAuthorizationRequest(
        OAuth2AuthorizationRequest authReq, HttpServletRequest req, HttpServletResponse res
    ) {
        if (authReq == null) {
            cookiesService.deleteCookies(req, res, OAuth2Cookie.getAllRequiredCookies());
            return;
        }
        final String serializedData = cookiesService.serializeValue(authReq);
        cookiesService.addCookie(res, createPayload(OAuth2Cookie.SESSION_PERSISTOR, serializedData));

        addReturnActionCookie(req, res, OAuth2Cookie.AFTER_LOGIN_REDIR_URL);
        addReturnActionCookie(req, res, OAuth2Cookie.AFTER_SIGNUP_REDIR_URL);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest req, HttpServletResponse res) {
        return loadAuthorizationRequest(req);
    }

    private void addReturnActionCookie(HttpServletRequest req, HttpServletResponse res, ConfigurableCookie cookie) {
        final String value = req.getParameter(cookie.getCookieName());
        if (StringUtils.isNoneBlank(value)) {
            final StringJoiner path = new StringJoiner("/")
                .add(req.getParameter(OAuth2Cookie.BASE_REDIR_URL.getCookieName()))
                .add(value);
            cookiesService.addCookie(res, createPayload(cookie, path.toString()));
        }
    }

    private CookiePayload createPayload(ConfigurableCookie cookie, String value) {
        return CookiePayload.builder()
            .cookie(cookie)
            .value(value)
            .maxAge(oAuth2Properties.getExpirationMinutes() * 60)
            .build();
    }
}
