/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.misc.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.visphere.lib.filter.JwtAuthenticationFilter;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.kafka.SyncQueueHandler;
import pl.visphere.lib.security.user.StatelesslessUserDetailsService;

@Configuration
@RequiredArgsConstructor
class SecurityBeans {
    private final SyncQueueHandler syncQueueHandler;

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(
        JwtService jwtService,
        UserDetailsService userDetailsService
    ) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService, syncQueueHandler);
    }

    @Bean
    StatelesslessUserDetailsService statelesslessUserDetailsService() {
        return new StatelesslessUserDetailsService(syncQueueHandler);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
