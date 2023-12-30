/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.visphere.chat.network.message.dto.MessagePayloadReqDto;
import pl.visphere.chat.network.message.dto.MessagePayloadResDto;
import pl.visphere.lib.security.user.AuthUserDetails;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    @Override
    public MessagePayloadResDto processMessage(long userId, long textChannelId, MessagePayloadReqDto payloadDto) {
        MessagePayloadResDto resDto;
        try {
            final ZonedDateTime messageTime = ZonedDateTime.now();

            // TODO: save record to database

            resDto = MessagePayloadResDto.builder()
                .userId(userId)
                .messageId(UUID.randomUUID().toString())
                .fullName(payloadDto.fullName())
                .profileImageUrl(payloadDto.profileImageUrl())
                .sendDate(messageTime)
                .message(payloadDto.message())
                .build();

            log.info("Successfully processed and saved message for user ID: '{}' in text channel with ID: '{}': '{}'",
                userId, textChannelId, resDto);
        } catch (Exception ex) {
            return null;
        }
        return resDto;
    }

    @Override
    public List<MessagePayloadResDto> getAllMessagesWithOffset(
        long textChannelId, int offset, int size, AuthUserDetails user
    ) {
        return List.of();
    }
}
