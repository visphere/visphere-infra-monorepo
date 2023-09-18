/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.discoveryserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig {
    private final Environment environment;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        final HttpSecurity removedCsrf = httpSecurity.csrf(AbstractHttpConfigurer::disable);
        final HttpSecurity modifiedConfig = Arrays.stream(environment.getActiveProfiles()).toList().contains("dev")
            ? removedCsrf
            .authorizeHttpRequests(request -> request
                .anyRequest().permitAll()
            )
            : removedCsrf
            .authorizeHttpRequests(request -> request
                .anyRequest().authenticated()
            );
        return modifiedConfig
            .httpBasic(withDefaults())
            .build();
    }
}
