/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.mfa;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.visphere.auth.network.LoginResDto;
import pl.visphere.auth.network.mfa.dto.MfaAuthenticatorDataResDto;
import pl.visphere.auth.network.mfa.dto.MfaCredentialsReqDto;
import pl.visphere.lib.BaseMessageResDto;

@RestController
@RequestMapping("/api/v1/auth/mfa")
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
}
