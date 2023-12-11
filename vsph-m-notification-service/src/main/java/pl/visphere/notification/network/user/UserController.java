/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.network.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.notification.network.user.dto.UserNotifSettingsResDto;

@RestController
@RequestMapping("/api/v1/notification/user")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @GetMapping("/settings/email")
    ResponseEntity<UserNotifSettingsResDto> getUserMailNotifsState(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(userService.getUserMailNotifsState(user));
    }

    @PatchMapping("/settings/email")
    ResponseEntity<BaseMessageResDto> toggleUserMailNotifsState(
        @RequestParam boolean enabled,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(userService.toggleUserMailNotifsState(enabled, user));
    }
}
