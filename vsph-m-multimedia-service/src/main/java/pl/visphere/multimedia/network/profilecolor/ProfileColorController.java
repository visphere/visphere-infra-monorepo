/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.profilecolor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.multimedia.dto.MessageWithResourcePathResDto;
import pl.visphere.multimedia.network.profilecolor.dto.UpdateProfileColorReqDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/multimedia/profile/color")
@RequiredArgsConstructor
public class ProfileColorController {
    private final ProfileColorService profileColorService;

    @GetMapping("/all")
    ResponseEntity<List<String>> getColors() {
        return ResponseEntity.ok(profileColorService.getColors());
    }

    @PatchMapping
    ResponseEntity<MessageWithResourcePathResDto> updateColor(
        @Valid @RequestBody UpdateProfileColorReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(profileColorService.updateProfileColor(reqDto, user));
    }

    @PatchMapping("/guild/{guildId}")
    ResponseEntity<MessageWithResourcePathResDto> updateGuildColor(
        @PathVariable long guildId,
        @Valid @RequestBody UpdateProfileColorReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(profileColorService.updateGuildProfileColor(guildId, reqDto, user));
    }
}
