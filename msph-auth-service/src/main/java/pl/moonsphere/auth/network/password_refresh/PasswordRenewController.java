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
import pl.moonsphere.lib.BaseMessageResDto;

@RestController
@RequestMapping("/api/v1/auth/password/renew")
@RequiredArgsConstructor
class PasswordRenewController {
    private final IPasswordRenewService passwordRenewService;

    @PostMapping("/request")
    ResponseEntity<BaseMessageResDto> request(@Valid @RequestBody AttemptReqDto reqDto) {
        return ResponseEntity.ok(passwordRenewService.request(reqDto));
    }

    @PostMapping("/{token}/verify")
    ResponseEntity<BaseMessageResDto> verify(@PathVariable String token) {
        return ResponseEntity.ok(passwordRenewService.verify(token));
    }

    @PostMapping("/resend")
    ResponseEntity<BaseMessageResDto> resend(@Valid @RequestBody AttemptReqDto reqDto) {
        return ResponseEntity.ok(passwordRenewService.resend(reqDto));
    }

    @PatchMapping("/change/{token}")
    ResponseEntity<BaseMessageResDto> change(@PathVariable String token, @Valid @RequestBody ChangeReqDto reqDto) {
        return ResponseEntity.ok(passwordRenewService.change(token, reqDto));
    }
}
