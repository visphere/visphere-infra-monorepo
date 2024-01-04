/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.visphere.chat.config.FileProperties;
import pl.visphere.chat.domain.chatmessage.ChatFileDefinition;
import pl.visphere.chat.domain.chatmessage.ChatMessageEntity;
import pl.visphere.chat.domain.chatmessage.ChatMessageRepository;
import pl.visphere.chat.domain.chatmessage.ChatPrimaryKey;
import pl.visphere.chat.exception.MessageException;
import pl.visphere.chat.i18n.LocaleSet;
import pl.visphere.chat.network.message.dto.MessagePayloadReqDto;
import pl.visphere.chat.network.message.dto.MessagePayloadResDto;
import pl.visphere.chat.network.message.dto.MessagesResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.exception.app.FileException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.multimedia.UserImagesIdentify;
import pl.visphere.lib.kafka.payload.multimedia.UsersImagesDetailsReqDto;
import pl.visphere.lib.kafka.payload.multimedia.UsersImagesDetailsResDto;
import pl.visphere.lib.kafka.payload.sphere.*;
import pl.visphere.lib.kafka.payload.user.UserDetails;
import pl.visphere.lib.kafka.payload.user.UsersDetailsReqDto;
import pl.visphere.lib.kafka.payload.user.UsersDetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.s3.S3Bucket;
import pl.visphere.lib.s3.S3Client;
import pl.visphere.lib.security.user.AuthUserDetails;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final SyncQueueHandler syncQueueHandler;
    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper;
    private final FileProperties fileProperties;
    private final S3Client s3Client;
    private final I18nService i18nService;

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
        final ByteBuffer previousState = nextPage != null && !Objects.equals(nextPage, StringUtils.EMPTY)
            ? com.datastax.oss.protocol.internal.util.Bytes.fromHexString(nextPage)
            : null;

        final CassandraPageRequest cassandraPageRequest = CassandraPageRequest
            .of(pageRequest, previousState);

        final Slice<ChatMessageEntity> pageableChatMessages = chatMessageRepository
            .findAllByKey_TextChannelId(textChannelId, cassandraPageRequest);

        final List<Long> userIds = pageableChatMessages.getContent().stream()
            .map(message -> message.getKey().getUserId())
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
            final ChatPrimaryKey key = chatMessage.getKey();
            final UserDetails details = userDetails.get(key.getUserId());
            final String profileImagePath = userImagesDetails.get(key.getUserId());
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
    @Transactional
    public MessagePayloadResDto processMessage(long userId, long textChannelId, MessagePayloadReqDto payloadDto) {
        MessagePayloadResDto resDto;
        try {
            final UUID messageId = UUID.randomUUID();
            resDto = createAndSaveNewMessage(payloadDto, userId, textChannelId, messageId);
        } catch (Exception ex) {
            return null;
        }
        return resDto;
    }

    @Override
    @Transactional
    public MessagePayloadResDto processFilesMessages(
        long textChannelId, String body, MultipartFile[] files, AuthUserDetails user
    ) {
        MessagePayloadResDto resDto;
        final Long guildId = checkTextChannelAndUserAssignments(user, textChannelId);
        try {
            final MessagePayloadReqDto reqDto = objectMapper.readValue(body, MessagePayloadReqDto.class);
            final UUID messageId = UUID.randomUUID();

            final int maxFilesCount = fileProperties.getMaxPerMessage();
            if (files.length > maxFilesCount) {
                throw new FileException.MaxFilesInRequestExceedException(files.length, maxFilesCount);
            }
            final List<ChatFileDefinition> chatFileDefinitions = new ArrayList<>(maxFilesCount);
            for (final MultipartFile file : files) {
                final String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
                final String resourceDir = new StringJoiner("/")
                    .add(String.valueOf(guildId))
                    .add(String.valueOf(textChannelId))
                    .add(messageId.toString())
                    .toString();

                final String contentType = file.getContentType();
                final List<String> multimediaMime = List.of("image", "audio", "video");

                final ObjectMetadata objectMetadata = new ObjectMetadata();
                if (contentType != null && multimediaMime.stream().noneMatch(contentType::contains)) {
                    // prevent show file content, force to download
                    objectMetadata.setContentDisposition("attachment; filename=\"" + file.getOriginalFilename() + "\"");
                }
                final String fullResourcePath = s3Client
                    .putRawObject(S3Bucket.ATTACHMENTS, file, resourceDir, fileName, objectMetadata);

                final ChatFileDefinition chatFileDefinition = ChatFileDefinition.builder()
                    .mimeType(contentType)
                    .originalName(file.getOriginalFilename())
                    .path(fullResourcePath)
                    .build();
                chatFileDefinitions.add(chatFileDefinition);
            }
            resDto = createAndSaveNewMessage(reqDto, user.getId(), textChannelId, messageId, chatFileDefinitions);
        } catch (JsonProcessingException ex) {
            throw new GenericRestException("Unable to process json body: '{}'. Cause: '{}'.", body, ex.getMessage());
        } catch (IOException ex) {
            throw new GenericRestException("Unable to process file. Cause: '{}'.", ex.getMessage());
        }
        return resDto;
    }

    @Override
    @Transactional
    public BaseMessageResDto deleteMessage(String messageId, long textChannelId, AuthUserDetails user) {
        final Long guildId = checkTextChannelAndUserAssignments(user, textChannelId);

        final GuildDetailsReqDto guildDetailsReqDto = GuildDetailsReqDto.builder()
            .guildId(guildId)
            .isModifiable(false)
            .build();

        final GuildDetailsResDto detailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_GUILD_DETAILS, guildDetailsReqDto, GuildDetailsResDto.class);

        final ChatMessageEntity chatMessage = chatMessageRepository
            .findByKey_TextChannelIdAndKey_Id(textChannelId, UUID.fromString(messageId))
            .orElseThrow(() -> new MessageException.MessageNotFoundException(messageId, user.getId()));

        final boolean isAuthor = Objects.equals(chatMessage.getKey().getUserId(), user.getId());
        final boolean isGuildOwner = Objects.equals(detailsResDto.ownerId(), user.getId());
        if (!isAuthor && !isGuildOwner) {
            throw new MessageException.MessageNotFoundException(messageId, user.getId());
        }
        List<ChatFileDefinition> attachments = new ArrayList<>();
        if (chatMessage.getFilesList() != null) {
            // copy elements array to prevent unexpected behavior on detached Hibernate bean
            attachments = List.copyOf(chatMessage.getFilesList());
        }
        chatMessageRepository.delete(chatMessage);

        if (!attachments.isEmpty()) {
            for (final ChatFileDefinition fileDefinition : attachments) {
                final String fileKey = fileDefinition
                    .getPath()
                    .replace(S3Bucket.ATTACHMENTS.getName() + "/", StringUtils.EMPTY);
                s3Client.deleteObject(S3Bucket.ATTACHMENTS, fileKey);
            }
            log.info("Successfully removed: '{}' resources for removing message with ID: '{}'.,",
                attachments.size(), messageId);
        }
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.MESSAGE_DELETED_RESPONSE_SUCCESS))
            .build();
    }

    private MessagePayloadResDto createAndSaveNewMessage(
        MessagePayloadReqDto payloadDto, long userId, long textChannelId, UUID messageId,
        List<ChatFileDefinition> filesList
    ) {
        final ZonedDateTime messageTime = ZonedDateTime.now();

        final ChatPrimaryKey primaryKey = ChatPrimaryKey.builder()
            .id(messageId)
            .textChannelId(textChannelId)
            .createdTimestamp(messageTime.toInstant())
            .userId(userId)
            .build();

        final ChatMessageEntity chatMessage = ChatMessageEntity.builder()
            .key(primaryKey)
            .message(payloadDto.message())
            .timeZone(messageTime.getZone())
            .filesList(filesList)
            .build();

        chatMessageRepository.save(chatMessage);
        final MessagePayloadResDto resDto = messageMapper.mapToMessagePayload(chatMessage, payloadDto, userId);

        log.info("Successfully processed and saved message for user ID: '{}' in text channel with ID: '{}': '{}'",
            userId, textChannelId, resDto);
        return resDto;
    }

    private MessagePayloadResDto createAndSaveNewMessage(
        MessagePayloadReqDto payloadDto, long userId, long textChannelId, UUID messageId
    ) {
        return createAndSaveNewMessage(payloadDto, userId, textChannelId, messageId, List.of());
    }

    private Long checkTextChannelAndUserAssignments(AuthUserDetails user, long textChannelId) {
        final GuildByTextChannelIdResDto guild = syncQueueHandler.sendNotNullWithBlockThread(
            QueueTopic.GET_GUILD_BASE_TEXT_CHANNEL_ID, textChannelId, GuildByTextChannelIdResDto.class);

        final GuildAssignmentsReqDto assignmentsReqDto = GuildAssignmentsReqDto.builder()
            .userId(user.getId())
            .guildId(guild.id())
            .throwingError(true)
            .build();
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.CHECK_USER_GUILD_ASSIGNMENTS, assignmentsReqDto);

        return guild.id();
    }
}
