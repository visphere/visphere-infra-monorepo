/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.SecurityService;
import pl.visphere.lib.security.StatelessAuthenticationProvider;
import pl.visphere.lib.security.user.StatelesslessUserDetailsService;
import pl.visphere.oauth2.core.OAuth2Properties;
import pl.visphere.oauth2.core.repository.OAuth2Repository;
import pl.visphere.oauth2.core.resolver.OAuth2FailureResolver;
import pl.visphere.oauth2.core.resolver.OAuth2SuccessResolver;
import pl.visphere.oauth2.core.user.service.OAuth2UserServiceImpl;
import pl.visphere.oauth2.core.user.service.OidcUserServiceImpl;

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

    private final OAuth2Repository oAuth2Repository;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OidcUserServiceImpl oidcUserService;
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> configureOAuth2AccessToken;
    private final OAuth2SuccessResolver oAuth2SuccessResolver;
    private final OAuth2FailureResolver oAuth2FailureResolver;
    private final OAuth2Properties oAuth2Properties;

    private final String[] unsecuredMatchers = {
        "/oauth2/**",
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return securityService()
            .configureStatelessSecurity(httpSecurity, "/**", security -> security
                .oauth2Login(oAuth2LoginConfigurer -> oAuth2LoginConfigurer
                    .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                        .authorizationRequestRepository(oAuth2Repository)
                    )
                    .redirectionEndpoint(redirectionEndpointConfig -> redirectionEndpointConfig
                        .baseUri(oAuth2Properties.getSpringSecurityRedirectUri())
                    )
                    .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .oidcUserService(oidcUserService)
                        .userService(oAuth2UserService)
                    )
                    .tokenEndpoint(tokenEndpointConfig -> tokenEndpointConfig
                        .accessTokenResponseClient(configureOAuth2AccessToken)
                    )
                    .successHandler(oAuth2SuccessResolver)
                    .failureHandler(oAuth2FailureResolver)
                ))
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
