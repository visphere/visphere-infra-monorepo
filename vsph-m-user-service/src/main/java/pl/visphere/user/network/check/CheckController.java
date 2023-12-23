/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.check;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.user.network.check.dto.CheckAlreadyExistResDto;
import pl.visphere.user.network.check.dto.MyAccountReqDto;
import pl.visphere.user.network.check.dto.MyAccountResDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/check")
@RequiredArgsConstructor
class CheckController {
    private final CheckService checkService;

    @GetMapping("/prop/exist")
    ResponseEntity<CheckAlreadyExistResDto> checkIfAccountPropAlreadyExist(
        @RequestParam AccountValueParam by,
        @RequestParam String value
    ) {
        return ResponseEntity.ok(checkService.checkIfAccountPropAlreadyExist(by, value));
    }

    @GetMapping("/logged/prop/exist")
    ResponseEntity<CheckAlreadyExistResDto> checkIfLoggedAccountPropAlreadyExist(
        @RequestParam AccountValueParam by,
        @RequestParam String value,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(checkService.checkIfLoggedAccountPropAlreadyExist(by, value, user));
    }

    @PatchMapping("/myaccounts/exists")
    ResponseEntity<List<MyAccountResDto>> checkIfMyAccountsExists(@RequestBody List<MyAccountReqDto> reqDtos) {
        return ResponseEntity.ok(checkService.checkIfMyAccountsExists(reqDtos));
    }
}
