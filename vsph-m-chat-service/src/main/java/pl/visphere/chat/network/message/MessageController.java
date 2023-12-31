/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.chat.network.message.dto.MessagesResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;

@RestController
@RequestMapping("/api/v1/chat/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/textChannel/{textChannelId}/all")
    ResponseEntity<MessagesResDto> getAllMessagesWithOffset(
        @PathVariable long textChannelId,
        @RequestParam int offset,
        @RequestParam int size,
        @RequestParam String nextPage,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(messageService.getAllMessagesWithOffset(textChannelId, offset, size, nextPage, user));
    }
}
