/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.LocaleResolver;
import pl.visphere.lib.i18n.I18nService;

import java.io.IOException;

public class AuthResolver extends AbstractAuthResolver implements AuthenticationEntryPoint {
    public AuthResolver(I18nService i18nService, LocaleResolver localeResolver) {
        super(i18nService, localeResolver);
    }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) throws IOException {
        chainRequest(req, res);
    }
}
