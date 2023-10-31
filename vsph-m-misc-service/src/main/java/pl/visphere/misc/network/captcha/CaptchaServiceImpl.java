/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.misc.network.captcha;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.misc.exception.CaptchaException;
import pl.visphere.misc.i18n.LocaleSet;
import pl.visphere.misc.network.captcha.dto.CaptchaVerifyReqDto;
import pl.visphere.misc.network.captcha.dto.RestCaptchaVerificationResDto;

@Service
@RequiredArgsConstructor
class CaptchaServiceImpl implements CaptchaService {
    private final I18nService i18nService;
    private final RestTemplate restTemplate;
    private final CaptchaProperties captchaProperties;

    @Override
    public BaseMessageResDto verify(CaptchaVerifyReqDto reqDto) {
        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("secret", captchaProperties.getSecretkey());
        formData.add("response", reqDto.getResponse());
        formData.add("remoteip", reqDto.getRemoteIp());
        formData.add("sitekey", reqDto.getSiteKey());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
        final ResponseEntity<RestCaptchaVerificationResDto> response = restTemplate
            .exchange("https://hcaptcha.com/siteverify", HttpMethod.POST, request, RestCaptchaVerificationResDto.class);

        final RestCaptchaVerificationResDto resBody = response.getBody();
        if (resBody == null || (!resBody.success() && !reqDto.getSiteKey().equals(captchaProperties.getDevSitekey()))) {
            throw new CaptchaException.CaptchaVerificationException(reqDto);
        }
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CAPTCHA_RESPONSE_SUCCESS))
            .build();
    }
}
