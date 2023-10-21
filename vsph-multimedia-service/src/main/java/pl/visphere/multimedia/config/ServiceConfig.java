/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.visphere.lib.jwt.JwtService;

@Configuration
class ServiceConfig {
    @Bean
    JwtService jwtService(Environment environment) {
        return new JwtService(environment);
    }
}
