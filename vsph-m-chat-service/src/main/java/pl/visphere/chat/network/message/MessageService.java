/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import org.springframework.web.multipart.MultipartFile;
import pl.visphere.chat.network.message.dto.MessagePayloadReqDto;
import pl.visphere.chat.network.message.dto.MessagePayloadResDto;
import pl.visphere.chat.network.message.dto.MessagesResDto;
import pl.visphere.lib.security.user.AuthUserDetails;

public interface MessageService {
    MessagesResDto getAllMessagesWithOffset(long textChannelId, int offset, int size, String nextPage, AuthUserDetails user);
    MessagePayloadResDto processMessage(long userId, long textChannelId, MessagePayloadReqDto payloadDto);
    MessagePayloadResDto processFilesMessages(long userId, long textChannelId, String body, MultipartFile[] files);
}
