/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.SecurityService;
import pl.visphere.lib.security.StatelessAuthenticationProvider;
import pl.visphere.lib.security.user.StatelesslessUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig {
    private final LocaleResolver localeResolver;
    private final I18nService i18nService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final StatelesslessUserDetailsService statelesslessUserDetailsService;
    private final SyncQueueHandler syncQueueHandler;

    private final String[] unsecuredMatchers = {
        "/api/v1/auth/identity/login",
        "/api/v1/auth/identity/refresh",
        "/api/v1/auth/account/new",
        "/api/v1/auth/account/activate/{token}",
        "/api/v1/auth/account/activate/resend",
        "/api/v1/auth/account/enable",
        "/api/v1/auth/password/renew/request",
        "/api/v1/auth/password/renew/{token}/verify",
        "/api/v1/auth/password/renew/resend",
        "/api/v1/auth/password/renew/change/{token}",
        "/api/v1/auth/check/prop/exist",
        "/api/v1/auth/check/myaccounts/exists",
        "/api/v1/auth/mfa/authenticator/data",
        "/api/v1/auth/mfa/authenticator/set/{code}",
        "/api/v1/auth/mfa/authenticator/verify/{code}",
        "/api/v1/auth/mfa/alternative/email",
        "/api/v1/auth/mfa/alternative/email/{token}/validate"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return securityService()
            .configureStatelessSecurity(httpSecurity, "api/v1/auth/**")
            .build();
    }

    @Bean
    SecurityService securityService() {
        return new SecurityService(handlerExceptionResolver, i18nService, localeResolver, jwtService,
            statelesslessUserDetailsService, syncQueueHandler, unsecuredMatchers);
    }

    @Bean
    AuthenticationManager authenticationManager(
        MessageSource messageSource,
        PasswordEncoder passwordEncoder,
        StatelesslessUserDetailsService statelesslessUserDetailsService
    ) {
        return new StatelessAuthenticationProvider(messageSource, passwordEncoder, statelesslessUserDetailsService)
            .createManager();
    }
}
