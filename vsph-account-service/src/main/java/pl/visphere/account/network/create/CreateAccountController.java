/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.create;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.account.network.create.dto.ActivateAccountReqDto;
import pl.visphere.account.network.create.dto.ActivateAccountResDto;
import pl.visphere.account.network.create.dto.CreateAccountReqDto;
import pl.visphere.lib.BaseMessageResDto;

@RestController
@RequestMapping("/api/v1/account/new")
@RequiredArgsConstructor
public class CreateAccountController {
    private final CreateAccountService createAccountService;

    @PostMapping("/create")
    ResponseEntity<BaseMessageResDto> createNew(@Valid @RequestBody CreateAccountReqDto reqDto) {
        return new ResponseEntity<>(createAccountService.createNew(reqDto), HttpStatus.CREATED);
    }

    @PatchMapping("/activate/{token}")
    ResponseEntity<ActivateAccountResDto> activate(
        @PathVariable String token,
        @Valid @RequestBody ActivateAccountReqDto reqDto
    ) {
        return ResponseEntity.ok(createAccountService.activate(token, reqDto));
    }

    @PostMapping("/activate/resend")
    ResponseEntity<BaseMessageResDto> resend(@Valid @RequestBody ActivateAccountReqDto reqDto) {
        return ResponseEntity.ok(createAccountService.resend(reqDto));
    }
}
