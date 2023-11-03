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
import pl.visphere.sphere.network.guildlink.dto.CreateGuildLinkReqDto;
import pl.visphere.sphere.network.guildlink.dto.GuildLinkResDto;
import pl.visphere.sphere.network.guildlink.dto.UpdateGuildLinkActiveReqDto;
import pl.visphere.sphere.network.guildlink.dto.UpdateGuildLinkExpirationReqDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sphere/link")
@RequiredArgsConstructor
class GuildLinkController {
    private final GuildLinkService guildLinkService;

    @GetMapping("/guild/{guildId}")
    ResponseEntity<List<GuildLinkResDto>> getAllLinksFromGuild(
        @LoggedUser AuthUserDetails user,
        @PathVariable Long guildId
    ) {
        return ResponseEntity.ok(guildLinkService.getAllLinksFromGuild(user, guildId));
    }

    @PostMapping("/guild/{guildId}")
    ResponseEntity<BaseMessageResDto> createGuildLink(
        @LoggedUser AuthUserDetails user,
        @Valid @RequestBody CreateGuildLinkReqDto reqDto,
        @PathVariable Long guildId
    ) {
        return new ResponseEntity<>(guildLinkService.createGuildLink(user, reqDto, guildId), HttpStatus.CREATED);
    }

    @PatchMapping("/{linkId}/expiration")
    ResponseEntity<BaseMessageResDto> updateExpiration(
        @LoggedUser AuthUserDetails user,
        @Valid @RequestBody UpdateGuildLinkExpirationReqDto reqDto,
        @PathVariable Long linkId
    ) {
        return ResponseEntity.ok(guildLinkService.updateExpiration(user, reqDto, linkId));
    }

    @PatchMapping("/{linkId}/active")
    ResponseEntity<BaseMessageResDto> updateActive(
        @LoggedUser AuthUserDetails user,
        @Valid @RequestBody UpdateGuildLinkActiveReqDto reqDto,
        @PathVariable Long linkId
    ) {
        return ResponseEntity.ok(guildLinkService.updateActive(user, reqDto, linkId));
    }

    @DeleteMapping("/{linkId}")
    ResponseEntity<BaseMessageResDto> deleteGuildLink(
        @LoggedUser AuthUserDetails user,
        @PathVariable Long linkId
    ) {
        return ResponseEntity.ok(guildLinkService.deleteGuildLink(user, linkId));
    }
}
