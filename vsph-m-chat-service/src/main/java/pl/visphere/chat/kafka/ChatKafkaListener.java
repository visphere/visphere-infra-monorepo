/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.visphere.chat.service.MessagesService;
import pl.visphere.lib.kafka.payload.chat.DeleteTextChannelMessagesReqDto;
import pl.visphere.lib.kafka.sync.SyncListenerHandler;

@Slf4j
@Component
@RequiredArgsConstructor
class ChatKafkaListener {
    private final MessagesService messagesService;
    private final SyncListenerHandler syncListenerHandler;

    @KafkaListener(topics = "${visphere.kafka.topic.delete-user-messages}")
    void deleteUserMessagesListener(Message<Long> payload) {
        syncListenerHandler.parseAndSendResponse(payload, messagesService::deleteUserMessages);
    }

    @KafkaListener(topics = "${visphere.kafka.topic.delete-text-channel-messages}")
    void deleteTextChannelMessagesListener(Message<DeleteTextChannelMessagesReqDto> payload) {
        syncListenerHandler.parseAndSendResponse(payload, messagesService::deleteTextChannelMessages);
    }
}
