/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.service;

import pl.visphere.lib.kafka.payload.chat.DeleteTextChannelMessagesReqDto;
import pl.visphere.lib.kafka.payload.chat.DeleteUserMessagesReqDto;

public interface MessagesService {
    void deleteUserMessages(DeleteUserMessagesReqDto reqDto);
    void deleteTextChannelMessages(DeleteTextChannelMessagesReqDto reqDto);
}
