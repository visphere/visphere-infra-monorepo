/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guildlink;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.sphere.network.guildlink.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sphere/link")
@RequiredArgsConstructor
class GuildLinkController {
    private final GuildLinkService guildLinkService;

    @GetMapping("/guild/{guildId}/all")
    ResponseEntity<AllGuildJoinLinksResDto> getAllLinksFromGuild(
        @PathVariable long guildId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(guildLinkService.getAllLinksFromGuild(guildId, user));
    }

    @GetMapping("/expirations/timestamps/all")
    ResponseEntity<List<ExpireTimestamp>> getAllExpiredTimestamps() {
        return ResponseEntity.ok(guildLinkService.getAllExpiredTimestamps());
    }

    @GetMapping("/{linkId}")
    ResponseEntity<GuildLinkDetailsResDto> getGuildLinkDetails(
        @PathVariable long linkId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(guildLinkService.getGuildLinkDetails(linkId, user));
    }

    @PostMapping("/guild/{guildId}")
    ResponseEntity<BaseMessageResDto> createGuildLink(
        @PathVariable long guildId,
        @Valid @RequestBody CreateGuildLinkReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return new ResponseEntity<>(guildLinkService.createGuildLink(guildId, reqDto, user), HttpStatus.CREATED);
    }

    @PatchMapping("/{linkId}")
    ResponseEntity<BaseMessageResDto> updateGuildLink(
        @PathVariable long linkId,
        @Valid @RequestBody UpdateGuildLinkReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(guildLinkService.updateGuildLink(linkId, reqDto, user));
    }

    @PatchMapping("/{linkId}/activity")
    ResponseEntity<BaseMessageResDto> updateGuildLinkActivity(
        @PathVariable long linkId,
        @RequestParam boolean active,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(guildLinkService.updateGuildLinkActiveState(linkId, active, user));
    }

    @DeleteMapping("/{linkId}")
    ResponseEntity<BaseMessageResDto> deleteGuildLink(
        @PathVariable long linkId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(guildLinkService.deleteGuildLink(linkId, user));
    }
}
