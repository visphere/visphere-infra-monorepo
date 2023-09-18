/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.access;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.moonsphere.auth.network.access.dto.LoginPasswordReqDto;
import pl.moonsphere.auth.network.access.dto.LoginResDto;
import pl.moonsphere.auth.network.access.dto.RefreshReqDto;
import pl.moonsphere.auth.network.access.dto.RefreshResDto;
import pl.moonsphere.lib.BaseMessageResDto;

@RestController
@RequestMapping("/api/v1/auth/access")
@RequiredArgsConstructor
class AccessController {
    private final IAccessService accessService;

    @PostMapping("/login")
    ResponseEntity<LoginResDto> loginViaPassword(@Valid @RequestBody LoginPasswordReqDto reqDto) {
        return ResponseEntity.ok(accessService.loginViaPassword(reqDto));
    }

    @PostMapping("/login/token")
    ResponseEntity<LoginResDto> loginViaAccessToken() {
        return ResponseEntity.ok(accessService.loginViaAccessToken(1L));
    }

    @PatchMapping("/refresh")
    ResponseEntity<RefreshResDto> refresh(@Valid @RequestBody RefreshReqDto reqDto) {
        return ResponseEntity.ok(accessService.refresh(reqDto));
    }

    @DeleteMapping("/logout")
    ResponseEntity<BaseMessageResDto> logout() {
        return ResponseEntity.ok(accessService.logout(1L));
    }
}
