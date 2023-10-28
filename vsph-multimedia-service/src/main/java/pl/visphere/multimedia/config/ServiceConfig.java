/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.config;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.s3.S3Client;

@Configuration
class ServiceConfig {
    @Bean
    JwtService jwtService(Environment environment) {
        return new JwtService(environment);
    }

    @Bean
    AmazonS3 amazonS3(Environment environment) {
        return new S3Client(environment).initialize();
    }
}
