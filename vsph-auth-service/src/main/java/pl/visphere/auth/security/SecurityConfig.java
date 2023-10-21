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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import pl.visphere.lib.filter.JwtAuthenticationFilter;
import pl.visphere.lib.i18n.I18nService;
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
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        securityService().configureStatelessSecurity(httpSecurity, "api/v1/auth/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/identity/login").permitAll()
                .requestMatchers("/api/v1/auth/identity/refresh").permitAll()
                .requestMatchers("/api/v1/auth/password/renew/request").permitAll()
                .requestMatchers("/api/v1/auth/password/renew/{token}/verify").permitAll()
                .requestMatchers("/api/v1/auth/password/renew/resend").permitAll()
                .requestMatchers("/api/v1/auth/password/renew/change/{token}").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    SecurityService securityService() {
        return new SecurityService(handlerExceptionResolver, i18nService, localeResolver);
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
