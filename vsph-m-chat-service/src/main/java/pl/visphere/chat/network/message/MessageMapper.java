/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.visphere.chat.config.ExternalServiceConfig;
import pl.visphere.chat.domain.chatmessage.ChatFileDefinition;
import pl.visphere.chat.domain.chatmessage.ChatMessageEntity;
import pl.visphere.chat.domain.chatmessage.ChatPrimaryKey;
import pl.visphere.chat.network.message.dto.AttachmentFile;
import pl.visphere.chat.network.message.dto.MessagePayloadReqDto;
import pl.visphere.chat.network.message.dto.MessagePayloadResDto;
import pl.visphere.lib.kafka.payload.user.UserDetails;

import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
class MessageMapper {
    private final ExternalServiceConfig externalServiceConfig;

    MessagePayloadResDto mapToMessagePayload(
        ChatMessageEntity chatMessage, UserDetails details, String fullName, String profileImagePath, boolean isRemoved
    ) {
        final ChatPrimaryKey key = chatMessage.getKey();
        return MessagePayloadResDto.builder()
            .userId(key.getUserId())
            .messageId(key.getId().toString())
            .fullName(fullName)
            .profileImageUrl(profileImagePath)
            .sendDate(ZonedDateTime.ofInstant(key.getCreatedTimestamp(), chatMessage.getTimeZone()))
            .message(chatMessage.getMessage())
            .accountDeleted(details.accountDeleted() || isRemoved)
            .attachments(mapToAttachmentFilesList(chatMessage.getFilesList()))
            .build();
    }

    MessagePayloadResDto mapToMessagePayload(
        ChatMessageEntity chatMessage, MessagePayloadReqDto payloadDto, Long userId
    ) {
        final ChatPrimaryKey key = chatMessage.getKey();
        return MessagePayloadResDto.builder()
            .userId(userId)
            .messageId(key.getId().toString())
            .fullName(payloadDto.fullName())
            .profileImageUrl(payloadDto.profileImageUrl())
            .sendDate(key.getCreatedTimestamp().atZone(chatMessage.getTimeZone()))
            .message(chatMessage.getMessage())
            .accountDeleted(false)
            .attachments(mapToAttachmentFilesList(chatMessage.getFilesList()))
            .build();
    }

    List<AttachmentFile> mapToAttachmentFilesList(List<ChatFileDefinition> files) {
        return files == null ? List.of() : files.stream()
            .map(file -> AttachmentFile.builder()
                .mimeType(file.getMimeType())
                .originalName(file.getOriginalName())
                .path(externalServiceConfig.getS3Url() + "/" + file.getPath())
                .build()
            )
            .toList();
    }
}
