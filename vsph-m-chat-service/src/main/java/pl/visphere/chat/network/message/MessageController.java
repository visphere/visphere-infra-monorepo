/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.chat.network.message.dto.MessagePayloadResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/textChannel/{textChannelId}/all")
    ResponseEntity<List<MessagePayloadResDto>> getAllMessagesWithOffset(
        @PathVariable long textChannelId,
        @RequestParam int offset,
        @RequestParam int size,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(messageService.getAllMessagesWithOffset(textChannelId, offset, size, user));
    }
}
