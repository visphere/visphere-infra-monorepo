/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 *
 *     File name: CaptchaController.java
 *     Last modified: 9/3/23, 6:19 PM
 *     Project name: moonsphere-infra-monorepo
 *     Module name: msph-misc-service
 *
 * This project is a part of "MoonSphere" instant messenger system. This system is a part of
 * completing an engineers degree in computer science at Silesian University of Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */
package pl.moonsphere.misc.network.captcha;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class CaptchaController {
    private final ICaptchaService captchaService;

    @PostMapping("/verification")
    ResponseEntity<BaseMessageResDto> verify(@Valid @RequestBody CaptchaVerifyReqDto reqDto) {
        return new ResponseEntity<>(captchaService.verify(reqDto), HttpStatus.OK);
    }
}
