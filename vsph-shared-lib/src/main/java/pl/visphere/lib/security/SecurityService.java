/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import pl.visphere.lib.filter.MiddlewareExceptionFilter;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.resolver.AccessDeniedResolver;
import pl.visphere.lib.resolver.AuthResolver;

public class SecurityService {
    private final MiddlewareExceptionFilter middlewareExceptionFilter;
    private final AuthResolver authResolver;
    private final AccessDeniedResolver accessDeniedResolver;

    public SecurityService(
        HandlerExceptionResolver handlerExceptionResolver,
        I18nService i18nService,
        LocaleResolver localeResolver
    ) {
        this.middlewareExceptionFilter = new MiddlewareExceptionFilter(handlerExceptionResolver, localeResolver);
        this.authResolver = new AuthResolver(i18nService, localeResolver);
        this.accessDeniedResolver = new AccessDeniedResolver(i18nService, localeResolver);
    }

    public HttpSecurity configureStatelessSecurity(HttpSecurity httpSecurity, String matcher) throws Exception {
        return httpSecurity
            .sessionManagement(options -> options.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(middlewareExceptionFilter, LogoutFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authResolver)
                .accessDeniedHandler(accessDeniedResolver)
            )
            .securityMatcher(matcher);
    }
}
