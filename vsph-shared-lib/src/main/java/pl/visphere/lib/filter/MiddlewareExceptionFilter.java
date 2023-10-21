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
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
public class MiddlewareExceptionFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver resolver;

    public MiddlewareExceptionFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        try {
            chain.doFilter(req, res);
        } catch (final Exception ex) {
            log.error("Filter chain exception resolver executed exception. Details: {}", ex.getMessage());
            resolver.resolveException(req, res, null, ex);
        }
    }
}
