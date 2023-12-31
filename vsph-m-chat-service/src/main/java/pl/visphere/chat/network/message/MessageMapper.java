/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import org.springframework.stereotype.Component;
import pl.visphere.chat.domain.ChatMessageEntity;
import pl.visphere.chat.network.message.dto.MessagePayloadReqDto;
import pl.visphere.chat.network.message.dto.MessagePayloadResDto;
import pl.visphere.lib.kafka.payload.user.UserDetails;

import java.time.ZonedDateTime;
import java.util.UUID;

@Component
class MessageMapper {
    MessagePayloadResDto mapToMessagePayload(
        ChatMessageEntity chatMessage, UserDetails details, String profileImagePath
    ) {
        return MessagePayloadResDto.builder()
            .userId(chatMessage.getUserId())
            .messageId(chatMessage.getId().toString())
            .fullName(details.fullName())
            .profileImageUrl(profileImagePath)
            .sendDate(ZonedDateTime.ofInstant(chatMessage.getCreatedTimestamp(), chatMessage.getTimeZone()))
            .message(chatMessage.getMessage())
            .accountDeleted(details.accountDeleted())
            .build();
    }

    MessagePayloadResDto mapToMessagePayload(
        MessagePayloadReqDto payloadDto, UUID messageId, Long userId, ZonedDateTime messageTime
    ) {
        return MessagePayloadResDto.builder()
            .userId(userId)
            .messageId(messageId.toString())
            .fullName(payloadDto.fullName())
            .profileImageUrl(payloadDto.profileImageUrl())
            .sendDate(messageTime)
            .message(payloadDto.message())
            .accountDeleted(false)
            .build();
    }
}
