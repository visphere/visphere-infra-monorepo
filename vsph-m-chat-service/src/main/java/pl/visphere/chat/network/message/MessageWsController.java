/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pl.visphere.chat.network.message.dto.MessagePayloadReqDto;
import pl.visphere.chat.network.message.dto.MessagePayloadResDto;

@Controller
@RequiredArgsConstructor
class MessageWsController {
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/inbound/{textChannelId}/user/{userId}")
    void sendMessage(
        @DestinationVariable long textChannelId,
        @DestinationVariable long userId,
        @Payload MessagePayloadReqDto payloadReqDto
    ) {
        final MessagePayloadResDto resDto = messageService.processMessage(userId, textChannelId, payloadReqDto);
        if (resDto != null) {
            simpMessagingTemplate.convertAndSend(String.format("/topic/outbound.%s", textChannelId), resDto);
        }
    }
}
