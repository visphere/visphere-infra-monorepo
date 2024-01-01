/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import pl.visphere.chat.domain.chatmessage.ChatMessageEntity;
import pl.visphere.chat.domain.chatmessage.ChatMessageRepository;
import pl.visphere.chat.network.message.dto.MessagePayloadReqDto;
import pl.visphere.chat.network.message.dto.MessagePayloadResDto;
import pl.visphere.chat.network.message.dto.MessagesResDto;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.multimedia.UserImagesIdentify;
import pl.visphere.lib.kafka.payload.multimedia.UsersImagesDetailsReqDto;
import pl.visphere.lib.kafka.payload.multimedia.UsersImagesDetailsResDto;
import pl.visphere.lib.kafka.payload.sphere.TextChannelAssignmentsReqDto;
import pl.visphere.lib.kafka.payload.user.UserDetails;
import pl.visphere.lib.kafka.payload.user.UsersDetailsReqDto;
import pl.visphere.lib.kafka.payload.user.UsersDetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.user.AuthUserDetails;

import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final SyncQueueHandler syncQueueHandler;
    private final MessageMapper messageMapper;

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public MessagesResDto getAllMessagesWithOffset(
        long textChannelId, int offset, int size, String nextPage, AuthUserDetails user
    ) {
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.CHECK_TEXT_CHANNEL_ASSIGNMENTS,
            new TextChannelAssignmentsReqDto(user.getId(), textChannelId));

        if (nextPage == null && offset != 0) {
            return new MessagesResDto(true);
        }
        final PageRequest pageRequest = PageRequest.of(offset, size);
        final ByteBuffer previousState = !Objects.equals(nextPage, StringUtils.EMPTY)
            ? com.datastax.oss.protocol.internal.util.Bytes.fromHexString(nextPage)
            : null;

        final CassandraPageRequest cassandraPageRequest = CassandraPageRequest
            .of(pageRequest, previousState);

        final Slice<ChatMessageEntity> pageableChatMessages = chatMessageRepository
            .findAllByTextChannelId(textChannelId, cassandraPageRequest);

        final List<Long> userIds = pageableChatMessages.getContent().stream()
            .map(ChatMessageEntity::getUserId)
            .distinct()
            .toList();

        final UsersDetailsResDto usersDetailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_USERS_DETAILS, new UsersDetailsReqDto(userIds),
                UsersDetailsResDto.class);

        final List<UserImagesIdentify> userImagesIdentifies = usersDetailsResDto.userDetails().entrySet().stream()
            .map(details -> UserImagesIdentify.builder()
                .userId(details.getKey())
                .externalSupplier(details.getValue().externalSupplier())
                .accountDeleted(details.getValue().accountDeleted())
                .build())
            .toList();

        final UsersImagesDetailsResDto usersImagesDetailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_USERS_IMAGES_DETAILS,
                new UsersImagesDetailsReqDto(userImagesIdentifies), UsersImagesDetailsResDto.class);

        final Map<Long, UserDetails> userDetails = usersDetailsResDto.userDetails();
        final Map<Long, String> userImagesDetails = usersImagesDetailsResDto.imagesDetails();

        if (userDetails.size() != userIds.size() || userImagesDetails.size() != userIds.size()) {
            return new MessagesResDto(false);
        }
        final List<MessagePayloadResDto> resDtos = new ArrayList<>(pageableChatMessages.getNumberOfElements());
        for (final ChatMessageEntity chatMessage : pageableChatMessages.getContent()) {
            final UserDetails details = userDetails.get(chatMessage.getUserId());
            final String profileImagePath = userImagesDetails.get(chatMessage.getUserId());
            if (details == null || profileImagePath == null) {
                continue;
            }
            resDtos.add(messageMapper.mapToMessagePayload(chatMessage, details, profileImagePath));
        }
        resDtos.sort(Comparator.comparing(MessagePayloadResDto::sendDate));

        final CassandraPageRequest cassandraNextPage = (CassandraPageRequest) pageableChatMessages.getPageable();
        final ByteBuffer pagingState = cassandraNextPage.getPagingState();

        log.info("Successfully found and processed: '{}' messages for text channel: '{}'.",
            resDtos.size(), textChannelId);

        return MessagesResDto.builder()
            .messages(resDtos)
            .paginationState(com.datastax.oss.protocol.internal.util.Bytes.toHexString(pagingState))
            .paginationEnd(pagingState == null)
            .build();
    }

    @Override
    public MessagePayloadResDto processMessage(long userId, long textChannelId, MessagePayloadReqDto payloadDto) {
        MessagePayloadResDto resDto;
        try {
            final ZonedDateTime messageTime = ZonedDateTime.now();
            final UUID messageId = UUID.randomUUID();

            final ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .id(messageId)
                .message(payloadDto.message())
                .createdTimestamp(messageTime.toInstant())
                .timeZone(messageTime.getZone())
                .userId(userId)
                .textChannelId(textChannelId)
                .build();

            chatMessageRepository.save(chatMessage);
            resDto = messageMapper.mapToMessagePayload(payloadDto, messageId, userId, messageTime);

            log.info("Successfully processed and saved message for user ID: '{}' in text channel with ID: '{}': '{}'",
                userId, textChannelId, resDto);
        } catch (Exception ex) {
            return null;
        }
        return resDto;
    }

}
