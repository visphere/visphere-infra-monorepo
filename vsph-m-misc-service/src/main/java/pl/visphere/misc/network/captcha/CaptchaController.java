/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.misc.network.captcha;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.misc.network.captcha.dto.CaptchaVerifyReqDto;

@RestController
@RequestMapping("/api/v1/misc/captcha")
@RequiredArgsConstructor
class CaptchaController {
    private final CaptchaService captchaService;

    @PostMapping("/verify")
    ResponseEntity<BaseMessageResDto> verify(@Valid @RequestBody CaptchaVerifyReqDto reqDto) {
        return ResponseEntity.ok(captchaService.verify(reqDto));
    }
}
