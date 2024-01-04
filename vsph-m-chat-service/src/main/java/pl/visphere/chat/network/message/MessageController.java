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

    @GetMapping("/textchannel/{textChannelId}/all")
    ResponseEntity<MessagesResDto> getAllMessagesWithOffset(
        @PathVariable long textChannelId,
        @RequestParam int offset,
        @RequestParam int size,
        @RequestParam String nextPage,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(messageService.getAllMessagesWithOffset(textChannelId, offset, size, nextPage, user));
    }

    @PostMapping("/textchannel/{textChannelId}")
    ResponseEntity<Void> publishMessageWithFiles(
        @PathVariable long textChannelId,
        @RequestParam("body") String body,
        @RequestParam("files") MultipartFile[] files,
        @LoggedUser AuthUserDetails user
    ) {
        final MessagePayloadResDto resDto = messageService.processFilesMessages(textChannelId, body, files, user);
        simpMessagingTemplate.convertAndSend(String.format("/topic/outbound.%s", textChannelId), resDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
