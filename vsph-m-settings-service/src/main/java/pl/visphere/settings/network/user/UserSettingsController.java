/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.network.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.settings.network.user.dto.RelatedValueReqDto;
import pl.visphere.settings.network.user.dto.UserRelatedSettingsResDto;

@RestController
@RequestMapping("/api/v1/settings/user")
@RequiredArgsConstructor
class UserSettingsController {
    private final UserSettingsService userSettingsService;

    @GetMapping("/settings")
    ResponseEntity<UserRelatedSettingsResDto> getUserSettings(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(userSettingsService.getUserSettings(user));
    }

    @PatchMapping("/relate/lang")
    ResponseEntity<BaseMessageResDto> relateLangWithUser(
        @Valid @RequestBody RelatedValueReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(userSettingsService.relateLangWithUser(reqDto, user));
    }

    @PatchMapping("/relate/theme")
    ResponseEntity<BaseMessageResDto> relateThemeWithUser(
        @Valid @RequestBody RelatedValueReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(userSettingsService.relateThemeWithUser(reqDto, user));
    }

    @PatchMapping("/push/notifications")
    ResponseEntity<BaseMessageResDto> pushNotificationsState(
        @RequestParam boolean enabled,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(userSettingsService.updatePushNotificationsSettings(enabled, user));
    }

    @PatchMapping("/push/notifications/sound")
    ResponseEntity<BaseMessageResDto> pushNotificationsSoundState(
        @RequestParam boolean enabled,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(userSettingsService.updatePushNotificationsSoundSettings(enabled, user));
    }
}
