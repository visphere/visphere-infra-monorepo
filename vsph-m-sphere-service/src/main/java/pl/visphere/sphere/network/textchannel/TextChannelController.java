/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.textchannel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.sphere.network.textchannel.dto.CreateTextChannelReqDto;
import pl.visphere.sphere.network.textchannel.dto.TextChannelDetailsResDto;
import pl.visphere.sphere.network.textchannel.dto.TextChannelResDto;
import pl.visphere.sphere.network.textchannel.dto.UpdateTextChannelReqDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sphere/text-channel")
@RequiredArgsConstructor
public class TextChannelController {
    private final TextChannelService textChannelService;

    @GetMapping("/{textChannelId}/details")
    ResponseEntity<TextChannelDetailsResDto> getTextChannelDetails(
        @PathVariable long textChannelId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(textChannelService.getTextChannelDetails(textChannelId, user));
    }

    @GetMapping("/guild/{guildId}")
    ResponseEntity<List<TextChannelResDto>> getGuildTextChannels(
        @PathVariable long guildId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(textChannelService.getGuildTextChannels(guildId, user));
    }

    @PostMapping("/guild/{guildId}")
    ResponseEntity<BaseMessageResDto> createTextChannel(
        @PathVariable long guildId,
        @Valid @RequestBody CreateTextChannelReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return new ResponseEntity<>(textChannelService.createTextChannel(guildId, reqDto, user), HttpStatus.CREATED);
    }

    @PatchMapping("/{textChannelId}")
    ResponseEntity<BaseMessageResDto> updateTextChannel(
        @PathVariable long textChannelId,
        @Valid @RequestBody UpdateTextChannelReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(textChannelService.updateTextChannel(textChannelId, reqDto, user));
    }

    @DeleteMapping("/{textChannelId}")
    ResponseEntity<BaseMessageResDto> deleteTextChannel(
        @PathVariable long textChannelId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(textChannelService.deleteTextChannel(textChannelId, user));
    }
}
