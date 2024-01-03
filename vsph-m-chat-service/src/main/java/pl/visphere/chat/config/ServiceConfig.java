/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.visphere.lib.AbstractBaseServiceBeans;
import pl.visphere.lib.file.FileHelper;
import pl.visphere.lib.s3.S3Client;

@Configuration
class ServiceConfig extends AbstractBaseServiceBeans {
    @Bean
    FileHelper fileHelper() {
        return new FileHelper();
    }

    @Bean
    S3Client s3Client(Environment environment) {
        final S3Client s3Client = new S3Client(environment);
        s3Client.initialize();
        return s3Client;
    }
}
