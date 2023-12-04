/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.network.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.settings.network.user.dto.RelatedValueReqDto;

@RestController
@RequestMapping("/api/v1/settings/user")
@RequiredArgsConstructor
class UserSettingsController {
    private final UserSettingsService userSettingsService;

    @PatchMapping("/relate/lang")
    ResponseEntity<BaseMessageResDto> relateLangWithUser(
        @RequestBody RelatedValueReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(userSettingsService.relateLangWithUser(reqDto, user));
    }

    @PatchMapping("/relate/theme")
    ResponseEntity<BaseMessageResDto> relateThemeWithUser(
        @RequestBody RelatedValueReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(userSettingsService.relateThemeWithUser(reqDto, user));
    }
}
