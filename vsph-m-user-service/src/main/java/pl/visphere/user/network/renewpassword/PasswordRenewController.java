/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.renewpassword;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.user.network.renewpassword.dto.AttemptReqDto;
import pl.visphere.user.network.renewpassword.dto.ChangeReqDto;
import pl.visphere.user.network.renewpassword.dto.ChangeViaAccountReqDto;

@RestController
@RequestMapping("/api/v1/user/password/renew")
@RequiredArgsConstructor
class PasswordRenewController {
    private final PasswordRenewService passwordRenewService;

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

    @PatchMapping("/logged/change")
    ResponseEntity<BaseMessageResDto> changeViaAccount(
        HttpServletRequest req,
        @Valid @RequestBody ChangeViaAccountReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(passwordRenewService.changeViaAccount(req, reqDto, user));
    }
}
