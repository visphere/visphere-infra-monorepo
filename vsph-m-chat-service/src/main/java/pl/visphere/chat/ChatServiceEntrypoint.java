/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableCassandraRepositories
@EnableDiscoveryClient
@SpringBootApplication
public class ChatServiceEntrypoint {
    public static void main(String[] args) {
        SpringApplication.run(ChatServiceEntrypoint.class, args);
    }
}
