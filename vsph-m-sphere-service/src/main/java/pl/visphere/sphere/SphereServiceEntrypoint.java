/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableDiscoveryClient
@EnableJpaRepositories
@EnableCaching
@SpringBootApplication
public class SphereServiceEntrypoint {
    public static void main(String[] args) {
        SpringApplication.run(SphereServiceEntrypoint.class, args);
    }
}
