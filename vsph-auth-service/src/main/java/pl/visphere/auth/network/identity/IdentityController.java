/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.identity;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.auth.network.identity.dto.LoginPasswordReqDto;
import pl.visphere.auth.network.identity.dto.LoginResDto;
import pl.visphere.auth.network.identity.dto.RefreshReqDto;
import pl.visphere.auth.network.identity.dto.RefreshResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;

@RestController
@RequestMapping("/api/v1/auth/identity")
@RequiredArgsConstructor
class IdentityController {
    private final IdentityService accessService;

    @PostMapping("/login")
    ResponseEntity<LoginResDto> loginViaPassword(@Valid @RequestBody LoginPasswordReqDto reqDto) {
        return ResponseEntity.ok(accessService.loginViaPassword(reqDto));
    }

    @PostMapping("/login/token")
    ResponseEntity<LoginResDto> loginViaAccessToken(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(accessService.loginViaAccessToken(user));
    }

    @PatchMapping("/refresh")
    ResponseEntity<RefreshResDto> refresh(@Valid @RequestBody RefreshReqDto reqDto) {
        return ResponseEntity.ok(accessService.refresh(reqDto));
    }

    @DeleteMapping("/logout")
    ResponseEntity<BaseMessageResDto> logout(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(accessService.logout(user));
    }
}