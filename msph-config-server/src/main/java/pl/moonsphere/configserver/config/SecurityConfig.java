/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.configserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final Environment environment;

    // @formatter:off
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        final HttpSecurity modifiedConfig = Arrays.stream(environment.getActiveProfiles()).toList().contains("dev")
            ? httpSecurity
                .authorizeHttpRequests(request -> request.anyRequest().permitAll())
            : httpSecurity
                .authorizeHttpRequests(request -> request
                    .requestMatchers("/actuator/**").permitAll()
                    .anyRequest().authenticated()
                );
        return modifiedConfig
            .httpBasic(withDefaults())
            .build();
    }
    // @formatter:on
}
