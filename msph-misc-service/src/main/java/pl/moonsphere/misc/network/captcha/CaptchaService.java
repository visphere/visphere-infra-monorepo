/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 *
 *     File name: CaptchaService.java
 *     Last modified: 9/3/23, 6:20 PM
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

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pl.moonsphere.lib.BaseMessageResDto;
import pl.moonsphere.lib.i18n.I18nService;
import pl.moonsphere.misc.exception.CaptchaException;
import pl.moonsphere.misc.i18n.LocaleSet;
import pl.moonsphere.misc.network.captcha.dto.CaptchaVerifyReqDto;
import pl.moonsphere.misc.network.captcha.dto.RestCaptchaVerificationResDto;

@Service
@RefreshScope
@RequiredArgsConstructor
public class CaptchaService implements ICaptchaService {
    private final I18nService i18nService;
    private final RestTemplate restTemplate;
    private final CaptchaConfig captchaConfig;

    @Override
    public BaseMessageResDto verify(CaptchaVerifyReqDto reqDto) {
        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("secret", captchaConfig.getSecretkey());
        formData.add("response", reqDto.getResponse());
        formData.add("remoteip", reqDto.getRemoteIp());
        formData.add("sitekey", reqDto.getSiteKey());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
        final ResponseEntity<RestCaptchaVerificationResDto> response = restTemplate
            .exchange("https://hcaptcha.com/siteverify", HttpMethod.POST, request, RestCaptchaVerificationResDto.class);

        final RestCaptchaVerificationResDto resBody = response.getBody();
        if (resBody == null || (!resBody.success() && !reqDto.getSiteKey().equals(captchaConfig.getDevSitekey()))) {
            throw new CaptchaException.CaptchaVerificationException(reqDto);
        }
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CAPTCHA_RESPONSE_SUCCESS))
            .build();
    }
}
