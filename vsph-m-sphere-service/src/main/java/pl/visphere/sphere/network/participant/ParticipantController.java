/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.participant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.sphere.network.guild.dto.PasswordReqDto;
import pl.visphere.sphere.network.participant.dto.BannerMemberDetailsResDto;
import pl.visphere.sphere.network.participant.dto.GuildParticipantDetailsResDto;
import pl.visphere.sphere.network.participant.dto.GuildParticipantsResDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sphere/participant")
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;

    @GetMapping("/guild/{guildId}/all")
    ResponseEntity<GuildParticipantsResDto> getAllGuildParticipants(
        @PathVariable long guildId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(participantService.getAllGuildParticipants(guildId, user));
    }

    @GetMapping("/guild/{guildId}/banned/all")
    ResponseEntity<List<BannerMemberDetailsResDto>> getAllBannedGuildParticipants(
        @PathVariable long guildId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(participantService.getAllBannedParticipants(guildId, user));
    }

    @GetMapping("/guild/{guildId}/user/{userId}/details")
    ResponseEntity<GuildParticipantDetailsResDto> getGuildParticipantDetails(
        @PathVariable long guildId,
        @PathVariable long userId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(participantService.getGuildParticipantDetails(guildId, userId, user));
    }

    @DeleteMapping("/guild/{guildId}/leave")
    ResponseEntity<BaseMessageResDto> leaveGuild(
        @PathVariable long guildId,
        @RequestParam boolean deleteAllMessages,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(participantService.leaveGuild(guildId, deleteAllMessages, user));
    }

    @DeleteMapping("/guild/{guildId}/kick/user/{userId}")
    ResponseEntity<BaseMessageResDto> kickFromGuild(
        @PathVariable long guildId,
        @PathVariable long userId,
        @RequestParam boolean deleteAllMessages,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(participantService.kickFromGuild(guildId, userId, deleteAllMessages, user));
    }

    @PatchMapping("/guild/{guildId}/unban/user/{userId}")
    ResponseEntity<BaseMessageResDto> unbanFromGuild(
        @PathVariable long guildId,
        @PathVariable long userId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(participantService.unbanFromGuild(guildId, userId, user));
    }

    @PatchMapping("/guild/{guildId}/ban/user/{userId}")
    ResponseEntity<BaseMessageResDto> banFromGuild(
        @PathVariable long guildId,
        @PathVariable long userId,
        @RequestParam boolean deleteAllMessages,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(participantService.banFromGuild(guildId, userId, deleteAllMessages, user));
    }

    @PatchMapping("/guild/{guildId}/delegate/user/{userId}")
    ResponseEntity<BaseMessageResDto> delegateGuildProprietyToUser(
        @PathVariable long guildId,
        @PathVariable long userId,
        @Valid @RequestBody PasswordReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(participantService.delegateGuildProprietyToUser(guildId, userId, reqDto, user));
    }
}
