/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.email;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.user.network.email.dto.EmailAddressReqDto;
import pl.visphere.user.network.email.dto.SecondEmailAddressReqDto;

@RestController
@RequestMapping("/api/v1/user/email")
@RequiredArgsConstructor
class EmailController {
    private final EmailService emailService;

    @PostMapping("/first/request")
    ResponseEntity<BaseMessageResDto> requestChangeFirstEmailAddress(
        @Valid @RequestBody EmailAddressReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(emailService.requestUpdateFirstEmailAdrress(reqDto, user));
    }

    @PostMapping("/first/request/resend")
    ResponseEntity<BaseMessageResDto> requestResendChangeFirstEmailAddress(
        @Valid @RequestBody EmailAddressReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(emailService.requestResendUpdateFirstEmailAdrress(reqDto, user));
    }

    @PostMapping("/second/request")
    ResponseEntity<BaseMessageResDto> requestChangeSecondEmailAddress(
        @Valid @RequestBody SecondEmailAddressReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(emailService.requestUpdateSecondEmailAdrress(reqDto, user));
    }

    @PostMapping("/second/request/resend")
    ResponseEntity<BaseMessageResDto> requestResendChangeSecondEmailAddress(
        @Valid @RequestBody SecondEmailAddressReqDto reqDto,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(emailService.requestResendUpdateSecondEmailAdrress(reqDto, user));
    }

    @PatchMapping("/first/{token}")
    ResponseEntity<BaseMessageResDto> updateFirstEmailAddress(
        @Valid @RequestBody EmailAddressReqDto reqDto,
        @PathVariable String token,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(emailService.updateFirstEmailAddress(reqDto, token, user));
    }

    @PatchMapping("/second/{token}")
    ResponseEntity<BaseMessageResDto> updateSecondEmailAddress(
        @Valid @RequestBody SecondEmailAddressReqDto reqDto,
        @PathVariable String token,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(emailService.updateSecondEmailAddress(reqDto, token, user));
    }

    @DeleteMapping("/second")
    ResponseEntity<BaseMessageResDto> deleteSecondEmailAddress(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(emailService.deleteSecondEmailAddress(user));
    }
}
