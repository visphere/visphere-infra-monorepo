/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.identity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.user.network.LoginResDto;
import pl.visphere.user.network.identity.dto.LoginPasswordReqDto;
import pl.visphere.user.network.identity.dto.RefreshReqDto;
import pl.visphere.user.network.identity.dto.RefreshResDto;

@RestController
@RequestMapping("/api/v1/user/identity")
@RequiredArgsConstructor
class IdentityController {
    private final IdentityService accessService;

    @PostMapping("/login")
    ResponseEntity<LoginResDto> loginViaPassword(@Valid @RequestBody LoginPasswordReqDto reqDto) {
        return ResponseEntity.ok(accessService.loginViaPassword(reqDto));
    }

    @PostMapping("/login/token")
    ResponseEntity<LoginResDto> loginViaAccessToken(
        HttpServletRequest req,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(accessService.loginViaAccessToken(req, user));
    }

    @PatchMapping("/refresh")
    ResponseEntity<RefreshResDto> refresh(@Valid @RequestBody RefreshReqDto reqDto) {
        return ResponseEntity.ok(accessService.refresh(reqDto));
    }

    @DeleteMapping("/logout")
    ResponseEntity<BaseMessageResDto> logout(
        HttpServletRequest req,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(accessService.logout(req, user));
    }
}
