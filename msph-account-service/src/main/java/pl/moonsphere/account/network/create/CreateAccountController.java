/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.create;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.moonsphere.account.network.create.dto.ActivateAccountReqDto;
import pl.moonsphere.account.network.create.dto.ActivateAccountResDto;
import pl.moonsphere.account.network.create.dto.CreateAccountReqDto;
import pl.moonsphere.lib.BaseMessageResDto;

@RestController
@RequestMapping("/api/v1/account/new")
@RequiredArgsConstructor
public class CreateAccountController {
    private final ICreateAccountService createAccountService;

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
