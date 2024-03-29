/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.mfa;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.user.network.LoginResDto;
import pl.visphere.user.network.mfa.dto.MfaAuthenticatorDataResDto;
import pl.visphere.user.network.mfa.dto.MfaCredentialsReqDto;

@RestController
@RequestMapping("/api/v1/user/mfa")
@RequiredArgsConstructor
class MfaController {
    private final MfaService mfaService;

    @PostMapping("/authenticator/data")
    ResponseEntity<MfaAuthenticatorDataResDto> authenticatorData(@Valid @RequestBody MfaCredentialsReqDto reqDto) {
        return ResponseEntity.ok(mfaService.authenticatorData(reqDto));
    }

    @PatchMapping("/authenticator/verify/{code}")
    ResponseEntity<LoginResDto> authenticatorVerify(
        @PathVariable String code,
        @RequestParam boolean firstSetup,
        @Valid @RequestBody MfaCredentialsReqDto reqDto
    ) {
        return ResponseEntity.ok(mfaService.authenticatorSetOrVerify(code, reqDto, firstSetup));
    }

    @PostMapping("/alternative/email")
    ResponseEntity<BaseMessageResDto> alternativeSendEmail(@Valid @RequestBody MfaCredentialsReqDto reqDto) {
        return ResponseEntity.ok(mfaService.altSendEmail(reqDto));
    }

    @PatchMapping("/alternative/email/{token}/validate")
    ResponseEntity<LoginResDto> alternativeVerifyEmailToken(
        @PathVariable String token,
        @Valid @RequestBody MfaCredentialsReqDto reqDto
    ) {
        return ResponseEntity.ok(mfaService.altVerifyEmailToken(token, reqDto));
    }

    @PatchMapping("/settings/toggle")
    ResponseEntity<BaseMessageResDto> toggleMfaAccountState(
        @RequestParam boolean enabled,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(mfaService.toggleMfaAccountState(enabled, user));
    }

    @DeleteMapping("/reset")
    ResponseEntity<BaseMessageResDto> resetMfaAccountState(
        HttpServletRequest req,
        @RequestParam boolean logoutFromAll,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(mfaService.resetMfaSetup(req, logoutFromAll, user));
    }
}
