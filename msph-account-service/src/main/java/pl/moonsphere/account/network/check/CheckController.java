/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.check;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.moonsphere.account.network.check.dto.CheckAlreadyExistResDto;
import pl.moonsphere.account.network.check.dto.MyAccountReqDto;
import pl.moonsphere.account.network.check.dto.MyAccountResDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account/check")
@RequiredArgsConstructor
class CheckController {
    private final ICheckService checkService;

    @GetMapping("/prop/exist")
    ResponseEntity<CheckAlreadyExistResDto> checkIfUsernameAlreadyExist(
        @RequestParam AccountValueParam by,
        @RequestParam String value
    ) {
        return ResponseEntity.ok(checkService.checkIfAccountValueAlreadyExist(by, value));
    }

    @PatchMapping("/myaccounts/exists")
    ResponseEntity<List<MyAccountResDto>> checkIfMyAccountsExists(@RequestBody List<MyAccountReqDto> reqDtos) {
        return ResponseEntity.ok(checkService.checkIfMyAccountsExists(reqDtos));
    }
}
