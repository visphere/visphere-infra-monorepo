/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.chat.domain.chatmessage.ChatMessageRepository;
import pl.visphere.lib.kafka.payload.chat.DeleteTextChannelMessagesReqDto;
import pl.visphere.lib.kafka.payload.chat.DeleteUserMessagesReqDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagesServiceImpl implements MessagesService {
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public void deleteUserMessages(DeleteUserMessagesReqDto reqDto) {
        chatMessageRepository.deleteAllByKey_TextChannelIdInAndKey_UserId(reqDto.textChannelIds(), reqDto.userId());
        log.info("Successfully deleted messages from user and text channels: '{}' with ID: '{}'.",
            reqDto.textChannelIds(), reqDto.userId());
    }

    @Override
    @Transactional
    public void deleteTextChannelMessages(DeleteTextChannelMessagesReqDto reqDto) {
        chatMessageRepository.deleteAllByKey_TextChannelIdIn(reqDto.textChannelIds());
        log.info("Successfully deleted messages from text channel with IDs: '{}'.", reqDto.textChannelIds());
    }
}
