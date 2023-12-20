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

import java.util.List;

@RestController
@RequestMapping("/api/v1/sphere/guild")
@RequiredArgsConstructor
class GuildController {
    private final GuildService guildService;

    @GetMapping("/{guildId}/details")
    ResponseEntity<GuildDetailsResDto> getGuildDetails(@PathVariable long guildId, @LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(guildService.getGuildDetails(guildId, user));
    }

    @GetMapping("/{guildId}/owner/details")
    ResponseEntity<GuildOwnerDetailsResDto> getGuildOwnerDetails(
        @PathVariable long guildId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(guildService.getGuildOwnerDetails(guildId, user));
    }

    @GetMapping("/{guildId}/owner/overview")
    ResponseEntity<GuildOwnerOverviewResDto> getGuildOwnerOverview(
        @PathVariable long guildId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(guildService.getGuildOwnerOverview(guildId, user));
    }

    @GetMapping("/all")
    ResponseEntity<List<UserGuildResDto>> getAllGuildsForUser(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(guildService.getAllGuildsForUser(user));
    }

    @GetMapping("/categories")
    ResponseEntity<List<GuildCategoryResDto>> getGuildCategories() {
        return ResponseEntity.ok(guildService.getGuildCategories());
    }

    @PostMapping("/new")
    ResponseEntity<CreateGuildResDto> createGuild(
        @Valid @RequestBody CreateGuildReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return new ResponseEntity<>(guildService.createGuild(reqDto, user), HttpStatus.CREATED);
    }

    @PatchMapping("/{guildId}")
    ResponseEntity<BaseMessageResDto> updateGuild(
        @PathVariable long guildId,
        @LoggedUser AuthUserDetails user,
        @Valid @RequestBody UpdateGuildReqDto reqDto
    ) {
        return ResponseEntity.ok(guildService.updateGuild(guildId, reqDto, user));
    }

    @PatchMapping("/{guildId}/visibility")
    ResponseEntity<BaseMessageResDto> updateGuildVisibility(
        @PathVariable long guildId,
        @LoggedUser AuthUserDetails user,
        @Valid @RequestBody UpdateGuildVisibilityReqDto reqDto
    ) {
        return ResponseEntity.ok(guildService.updateGuildVisibility(guildId, reqDto, user));
    }

    @DeleteMapping("/{guildId}")
    ResponseEntity<BaseMessageResDto> deleteGuild(
        @PathVariable long guildId,
        @Valid @RequestBody PasswordReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(guildService.deleteGuild(guildId, reqDto, user));
    }
}
