/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.auth.network.account.dto.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;

@RestController
@RequestMapping("/api/v1/auth/account")
@RequiredArgsConstructor
class AccountController {
    private final AccountService accountService;

    @GetMapping("/details")
    ResponseEntity<AccountDetailsResDto> getAccountDetails(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(accountService.getAccountDetails(user));
    }

    @PatchMapping("/details")
    ResponseEntity<UpdateAccountDetailsResDto> updateAccountDetails(
        @Valid @RequestBody UpdateAccountDetailsReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(accountService.updateAccountDetails(reqDto, user));
    }

    @PostMapping("/new")
    ResponseEntity<BaseMessageResDto> createNew(@Valid @RequestBody CreateAccountReqDto reqDto) {
        return new ResponseEntity<>(accountService.createNew(reqDto), HttpStatus.CREATED);
    }

    @PatchMapping("/activate/{token}")
    ResponseEntity<BaseMessageResDto> activate(@PathVariable String token) {
        return ResponseEntity.ok(accountService.activate(token));
    }

    @PostMapping("/activate/resend")
    ResponseEntity<BaseMessageResDto> resend(@Valid @RequestBody ActivateAccountReqDto reqDto) {
        return ResponseEntity.ok(accountService.resend(reqDto));
    }

    @PostMapping("/disable")
    ResponseEntity<BaseMessageResDto> disable(
        @Valid @RequestBody PasswordReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(accountService.disable(reqDto, user));
    }

    @PostMapping("/enable")
    ResponseEntity<BaseMessageResDto> enable(HttpServletRequest req) {
        return ResponseEntity.ok(accountService.enable(req));
    }
}
