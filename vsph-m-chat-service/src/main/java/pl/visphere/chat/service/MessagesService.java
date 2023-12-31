/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.service;

import pl.visphere.lib.kafka.payload.chat.DeleteTextChannelMessagesReqDto;

public interface MessagesService {
    void deleteUserMessages(Long userId);
    void deleteTextChannelMessages(DeleteTextChannelMessagesReqDto reqDto);
}
