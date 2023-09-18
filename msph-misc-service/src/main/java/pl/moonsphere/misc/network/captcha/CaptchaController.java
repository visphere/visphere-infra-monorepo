/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.misc.network.captcha;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.moonsphere.lib.BaseMessageResDto;
import pl.moonsphere.misc.network.captcha.dto.CaptchaVerifyReqDto;

@RestController
@RequestMapping("/api/v1/misc/captcha")
@RequiredArgsConstructor
class CaptchaController {
    private final ICaptchaService captchaService;

    @PostMapping("/verify")
    ResponseEntity<BaseMessageResDto> verify(@Valid @RequestBody CaptchaVerifyReqDto reqDto) {
        return ResponseEntity.ok(captchaService.verify(reqDto));
    }
}
