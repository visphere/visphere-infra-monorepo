/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.security;

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
import pl.visphere.lib.security.SecurityBeanProvider;
import pl.visphere.lib.security.SecurityService;
import pl.visphere.lib.security.StatelessAuthenticationProvider;
import pl.visphere.lib.security.user.StatelesslessUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig implements SecurityBeanProvider {
    private final LocaleResolver localeResolver;
    private final I18nService i18nService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final StatelesslessUserDetailsService statelesslessUserDetailsService;
    private final SyncQueueHandler syncQueueHandler;

    @Override
    public String[] securityEntrypointMatchers() {
        return new String[]{
            "api/v1/user/**"
        };
    }

    @Override
    public String[] unsecureMatchers() {
        return new String[]{
            "/api/v1/user/identity/login",
            "/api/v1/user/identity/refresh",
            "/api/v1/user/account/new",
            "/api/v1/user/account/activate/{token}",
            "/api/v1/user/account/activate/resend",
            "/api/v1/user/account/enable",
            "/api/v1/user/password/renew/request",
            "/api/v1/user/password/renew/{token}/verify",
            "/api/v1/user/password/renew/resend",
            "/api/v1/user/password/renew/change/{token}",
            "/api/v1/user/check/prop/exist",
            "/api/v1/user/check/myaccounts/exists",
            "/api/v1/user/mfa/authenticator/data",
            "/api/v1/user/mfa/authenticator/set/{code}",
            "/api/v1/user/mfa/authenticator/verify/{code}",
            "/api/v1/user/mfa/alternative/email",
            "/api/v1/user/mfa/alternative/email/{token}/validate"
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return securityService()
            .configureStatelessSecurity(httpSecurity)
            .build();
    }

    @Bean
    SecurityService securityService() {
        return new SecurityService(handlerExceptionResolver, i18nService, localeResolver, jwtService,
            statelesslessUserDetailsService, syncQueueHandler, this);
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
