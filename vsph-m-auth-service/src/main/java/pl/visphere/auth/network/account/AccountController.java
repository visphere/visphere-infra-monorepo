/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.auth.network.account.dto.ActivateAccountReqDto;
import pl.visphere.auth.network.account.dto.ActivateAccountResDto;
import pl.visphere.auth.network.account.dto.CreateAccountReqDto;
import pl.visphere.lib.BaseMessageResDto;

@RestController
@RequestMapping("/api/v1/auth/account")
@RequiredArgsConstructor
class AccountController {
    private final AccountService accountService;

    @PostMapping("/new")
    ResponseEntity<BaseMessageResDto> createNew(@Valid @RequestBody CreateAccountReqDto reqDto) {
        return new ResponseEntity<>(accountService.createNew(reqDto), HttpStatus.CREATED);
    }

    @PatchMapping("/activate/{token}")
    ResponseEntity<ActivateAccountResDto> activate(
        @PathVariable String token
    ) {
        return ResponseEntity.ok(accountService.activate(token));
    }

    @PostMapping("/activate/resend")
    ResponseEntity<BaseMessageResDto> resend(@Valid @RequestBody ActivateAccountReqDto reqDto) {
        return ResponseEntity.ok(accountService.resend(reqDto));
    }
}
