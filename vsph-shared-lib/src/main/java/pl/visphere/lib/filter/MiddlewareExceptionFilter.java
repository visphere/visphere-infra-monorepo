/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Slf4j
public class MiddlewareExceptionFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver resolver;
    private final LocaleResolver localeResolver;

    public MiddlewareExceptionFilter(
        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
        LocaleResolver localeResolver
    ) {
        this.resolver = resolver;
        this.localeResolver = localeResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        try {
            final Locale locale = localeResolver.resolveLocale(req);
            LocaleContextHolder.setLocale(locale);
            chain.doFilter(req, res);
        } catch (final Exception ex) {
            log.error("Filter chain exception resolver executed exception. Details: {}", ex.getMessage());
            resolver.resolveException(req, res, null, ex);
        }
    }
}
