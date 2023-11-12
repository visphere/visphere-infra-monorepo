/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.oauth2.cookie.ConfigurableCookie;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum OAuth2Cookie implements ConfigurableCookie {
    SESSION_PERSISTOR("oauth2_auth_request"),
    BASE_REDIR_URL("base_uri"),
    AFTER_LOGIN_REDIR_URL("after_login_uri"),
    AFTER_SIGNUP_REDIR_URL("after_signup_uri"),
    ;

    private final String cookieName;

    static List<ConfigurableCookie> getAllRequiredCookies() {
        return List.of(SESSION_PERSISTOR, AFTER_LOGIN_REDIR_URL, AFTER_SIGNUP_REDIR_URL);
    }
}
