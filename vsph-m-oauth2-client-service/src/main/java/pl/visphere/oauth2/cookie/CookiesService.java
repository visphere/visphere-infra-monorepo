/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import pl.visphere.lib.exception.GenericRestException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class CookiesService {
    private static final String COOKIE_PATH = "/";

    public Optional<Cookie> getCookie(HttpServletRequest req, ConfigurableCookie cookie) {
        final Cookie[] reqCookies = req.getCookies();
        if (reqCookies == null) {
            log.warn("Cookie: '{}' not found.", cookie);
            return Optional.empty();
        }
        for (final Cookie reqCookie : reqCookies) {
            if (Objects.equals(cookie.getCookieName(), reqCookie.getName())) {
                log.info("Successfully find cookie: '{}'.", reqCookie);
                return Optional.of(reqCookie);
            }
        }
        log.warn("Cookie: '{}' not found.", cookie);
        return Optional.empty();
    }

    public void addCookie(HttpServletResponse res, CookiePayload payload) {
        final Cookie cookie = new Cookie(payload.cookie().getCookieName(), payload.value());
        cookie.setPath(COOKIE_PATH);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(payload.maxAge());
        res.addCookie(cookie);
        log.info("Successfully added cookie: '{}' to HttpServletResponse object.", cookie);
    }

    public void deleteCookie(HttpServletRequest req, HttpServletResponse res, ConfigurableCookie cookie) {
        final Cookie[] reqCookies = req.getCookies();
        if (reqCookies == null) {
            return;
        }
        for (final Cookie reqCookie : reqCookies) {
            if (Objects.equals(reqCookie.getName(), cookie.getCookieName())) {
                reqCookie.setValue(StringUtils.EMPTY);
                reqCookie.setPath(COOKIE_PATH);
                reqCookie.setMaxAge(0);
                res.addCookie(reqCookie);
                log.info("Successfully deleted cookie: '{}'.", reqCookie);
            }
        }
    }

    public void deleteCookies(HttpServletRequest req, HttpServletResponse res, List<ConfigurableCookie> cookies) {
        for (final ConfigurableCookie cookie : cookies) {
            deleteCookie(req, res, cookie);
        }
    }

    public String getCookieValueOrDefault(HttpServletRequest req, ConfigurableCookie cookie, String defaultValue) {
        return getCookie(req, cookie)
            .map(Cookie::getValue)
            .orElse(defaultValue);
    }

    public String serializeValue(Object data) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(data));
    }

    public <T> T deserializeValue(Cookie cookie, Class<T> objectClazz) {
        final byte[] bytes = Base64.getUrlDecoder().decode(cookie.getValue());
        T returnObj;
        try (final ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            returnObj = objectClazz.cast(inputStream.readObject());
        } catch (IOException | ClassNotFoundException ex) {
            throw new GenericRestException("Unable to deserialize cookie value. Cause: '{}'.", ex.getMessage());
        }
        return returnObj;
    }
}
