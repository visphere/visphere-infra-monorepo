/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.domain;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.payload.RequestDto;
import pl.visphere.lib.kafka.payload.ResponseDto;

@Component
public class TestKafka {

    @KafkaListener(topics = "${visphere.kafka.topic.check-user}")
    @SendTo
    public ResponseDto receive(RequestDto request) {
        System.out.println(request);
        return new ResponseDto("This is a response message");
    }
}
