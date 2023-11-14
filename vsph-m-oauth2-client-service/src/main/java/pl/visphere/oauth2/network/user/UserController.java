/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.oauth2.network.user.dto.GetFillDataResDto;
import pl.visphere.oauth2.network.user.dto.LoginResDto;
import pl.visphere.oauth2.network.user.dto.UpdateFillDataReqDto;

@RestController
@RequestMapping("/api/v1/oauth2/user")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @GetMapping("/data")
    ResponseEntity<GetFillDataResDto> getFillData(@RequestParam String token) {
        return ResponseEntity.ok(userService.getUserData(token));
    }

    @PatchMapping("/data/fill")
    ResponseEntity<LoginResDto> updateFillData(
        @Valid @RequestBody UpdateFillDataReqDto reqDto,
        @RequestParam String token
    ) {
        return ResponseEntity.ok(userService.fillUserData(reqDto, token));
    }

    @PostMapping("/login")
    ResponseEntity<LoginResDto> loginViaProvider(@RequestParam String token) {
        return ResponseEntity.ok(userService.loginViaProvider(token));
    }
}
