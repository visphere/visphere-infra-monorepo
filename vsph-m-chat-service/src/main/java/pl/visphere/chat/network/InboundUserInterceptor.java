/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.sphere.GuildAssignmentsReqDto;
import pl.visphere.lib.kafka.payload.user.CheckUserSessionReqDto;
import pl.visphere.lib.kafka.payload.user.CheckUserSessionResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;

import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class InboundUserInterceptor implements ChannelInterceptor {
    private final SyncQueueHandler syncQueueHandler;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            if (accessor == null) {
                return null;
            }
            if (Objects.equals(accessor.getCommand(), StompCommand.CONNECT)) {
                return onAuthenticateUser(message, accessor);
            }
        } catch (Exception ignored) {
            return null;
        }
        return message;
    }

    private Message<?> onAuthenticateUser(Message<?> message, StompHeaderAccessor accessor) {
        final CheckUserSessionReqDto sessionReqDto = CheckUserSessionReqDto.builder()
            .token(getHeaderValue(accessor, "X-Token"))
            .build();
        final CheckUserSessionResDto sessionResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.CHECK_USER_SESSION, sessionReqDto, CheckUserSessionResDto.class);
        if (sessionResDto.userId() == null) {
            return null;
        }
        final GuildAssignmentsReqDto assignmentsReqDto = GuildAssignmentsReqDto.builder()
            .userId(sessionResDto.userId())
            .guildId(Long.parseLong(getHeaderValue(accessor, "X-GuildId")))
            .build();
        final boolean isAssignment = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.CHECK_USER_GUILD_ASSIGNMENTS, assignmentsReqDto, Boolean.class);
        if (!isAssignment) {
            return null;
        }
        return message;
    }

    private String getHeaderValue(StompHeaderAccessor accessor, String name) {
        final List<String> header = accessor.getNativeHeader(name);
        if (header == null || header.size() != 1) {
            throw new MissingResourceException(StringUtils.EMPTY, InboundUserInterceptor.class.getName(), name);
        }
        return header.get(0);
    }
}
