/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.joinguild;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.sphere.network.joinguild.dto.JoinGuildResDto;
import pl.visphere.sphere.network.joinguild.dto.JoiningGuildDetailsResDto;

@RestController
@RequestMapping("/api/v1/sphere/join")
@RequiredArgsConstructor
class JoinGuildController {
    private final JoinGuildService joinGuildService;

    @GetMapping("/private/{code}/guild/details")
    ResponseEntity<JoiningGuildDetailsResDto> getPrivateGuildDetails(
        @PathVariable String code,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(joinGuildService.getPrivateGuildDetails(code, user));
    }

    @GetMapping("/public/guild/{guildId}/details")
    ResponseEntity<JoiningGuildDetailsResDto> getPublicGuildDetails(
        @PathVariable long guildId,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(joinGuildService.getPublicGuildDetails(guildId, user));
    }

    @PostMapping("/private/{code}")
    ResponseEntity<JoinGuildResDto> joinToPrivateGuildViaCode(
        @PathVariable String code,
        @LoggedUser AuthUserDetails user
    ) {
        return new ResponseEntity<>(joinGuildService.joinToPrivateGuildViaCode(code, user), HttpStatus.CREATED);
    }

    @PostMapping("/public/guild/{guildId}")
    ResponseEntity<JoinGuildResDto> joinToPrivateGuildViaCode(
        @PathVariable long guildId,
        @LoggedUser AuthUserDetails user
    ) {
        return new ResponseEntity<>(joinGuildService.joinToPublicGuild(guildId, user), HttpStatus.CREATED);
    }
}
