/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.chat.domain.ChatMessageRepository;
import pl.visphere.lib.kafka.payload.chat.DeleteTextChannelMessagesReqDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagesServiceImpl implements MessagesService {
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public void deleteUserMessages(Long userId) {
        chatMessageRepository.deleteAllByUserId(userId);
        log.info("Successfully deleted messages from user with ID: '{}'.", userId);
    }

    @Override
    public void deleteTextChannelMessages(DeleteTextChannelMessagesReqDto reqDto) {
        chatMessageRepository.deleteAllByTextChannelIdIn(reqDto.textChannelIds());
        log.info("Successfully deleted messages from text channel with IDs: '{}'.", reqDto.textChannelIds());
    }
}
