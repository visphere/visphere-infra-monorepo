/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.visphere.chat.network.message.dto.MessagePayloadResDto;
import pl.visphere.chat.network.message.dto.MessagesResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;

@RestController
@RequestMapping("/api/v1/chat/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

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

    @PostMapping("/textchannel/{textChannelId}/user/{userId}")
    ResponseEntity<Void> publishMessageWithFiles(
        @PathVariable long textChannelId,
        @PathVariable long userId,
        @RequestParam("body") String body,
        @RequestParam("files") MultipartFile[] files
    ) {
        final MessagePayloadResDto resDto = messageService.processFilesMessages(userId, textChannelId, body, files);
        simpMessagingTemplate.convertAndSend(String.format("/topic/outbound.%s", textChannelId), resDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
