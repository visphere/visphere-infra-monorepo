/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.sphere.network.guild.dto.*;

@RestController
@RequestMapping("/api/v1/sphere/guild")
@RequiredArgsConstructor
class GuildController {
    private final GuildService guildService;

    @PostMapping("/new")
    ResponseEntity<CreateGuildResDto> createGuild(
        @LoggedUser AuthUserDetails user,
        @Valid @RequestBody CreateGuildReqDto reqDto
    ) {
        return new ResponseEntity<>(guildService.createGuild(user, reqDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{guildId}/name")
    ResponseEntity<UpdateGuildResDto> updateGuildName(
        @LoggedUser AuthUserDetails user,
        @Valid @RequestBody UpdateGuildNameReqDto reqDto,
        @PathVariable Long guildId
    ) {
        return ResponseEntity.ok(guildService.updateGuildName(user, reqDto, guildId));
    }

    @PatchMapping("/{guildId}/category")
    ResponseEntity<BaseMessageResDto> updateGuildCategory(
        @LoggedUser AuthUserDetails user,
        @Valid @RequestBody UpdateGuildCategoryReqDto reqDto,
        @PathVariable Long guildId
    ) {
        return ResponseEntity.ok(guildService.updateGuildCategory(user, reqDto, guildId));
    }

    @PatchMapping("/{guildId}/visibility")
    ResponseEntity<BaseMessageResDto> updateGuildVisibility(
        @LoggedUser AuthUserDetails user,
        @Valid @RequestBody UpdateGuildVisibilityReqDto reqDto,
        @PathVariable Long guildId
    ) {
        return ResponseEntity.ok(guildService.updateGuildVisibility(user, reqDto, guildId));
    }
}
