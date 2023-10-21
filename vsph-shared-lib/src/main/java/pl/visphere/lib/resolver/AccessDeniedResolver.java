/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.LocaleResolver;
import pl.visphere.lib.i18n.I18nService;

import java.io.IOException;

public class AccessDeniedResolver extends AbstractAuthResolver implements AccessDeniedHandler {
    public AccessDeniedResolver(I18nService i18nService, LocaleResolver localeResolver) {
        super(i18nService, localeResolver);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
        chainRequest(req, res);
    }
}
