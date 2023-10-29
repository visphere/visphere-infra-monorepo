/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.visphere.lib.jwt.JwtService;

public abstract class AbstractBaseServiceBeans {
    @Bean
    JwtService jwtService(Environment environment) {
        return new JwtService(environment);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
