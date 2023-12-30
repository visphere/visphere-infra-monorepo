/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import pl.visphere.chat.network.message.dto.MessagePayloadReqDto;
import pl.visphere.chat.network.message.dto.MessagePayloadResDto;
import pl.visphere.lib.security.user.AuthUserDetails;

import java.util.List;

public interface MessageService {
    MessagePayloadResDto processMessage(long userId, long textChannelId, MessagePayloadReqDto payloadDto);
    List<MessagePayloadResDto> getAllMessagesWithOffset(long textChannelId, int offset, int size, AuthUserDetails user);
}
