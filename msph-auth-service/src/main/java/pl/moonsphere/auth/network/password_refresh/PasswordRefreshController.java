/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.password_refresh;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.moonsphere.auth.network.password_refresh.dto.AttemptReqDto;
import pl.moonsphere.auth.network.password_refresh.dto.ChangeReqDto;
import pl.moonsphere.lib.dto.BaseMessageResDto;
import pl.moonsphere.lib.dto.BaseVerificationResDto;

@RestController
@RequestMapping("/api/v1/auth/password/refresh")
@RequiredArgsConstructor
class PasswordRefreshController {
    private final IPasswordRefreshService passwordRefreshService;

    @PostMapping("/request")
    ResponseEntity<BaseMessageResDto> request(@Valid @RequestBody AttemptReqDto reqDto) {
        return ResponseEntity.ok(passwordRefreshService.request(reqDto));
    }

    @GetMapping("/{token}/verify")
    ResponseEntity<BaseVerificationResDto> verify(@PathVariable String token) {
        return ResponseEntity.ok(passwordRefreshService.verify(token));
    }

    @PatchMapping("/change/{token}")
    ResponseEntity<BaseMessageResDto> change(@PathVariable String token, @Valid @RequestBody ChangeReqDto reqDto) {
        return ResponseEntity.ok(passwordRefreshService.change(token, reqDto));
    }
}
