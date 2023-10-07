/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.domain;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestKafka {

    @KafkaListener(topics = "testtopic")
    public void receive(ConsumerRecord<String, String> consumerRecord) {
        System.out.println("Received payload " + consumerRecord.toString());
    }
}
