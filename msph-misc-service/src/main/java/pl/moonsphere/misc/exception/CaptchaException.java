/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.misc.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.moonsphere.lib.exception.AbstractRestException;
import pl.moonsphere.misc.i18n.LocaleSet;
import pl.moonsphere.misc.network.captcha.dto.CaptchaVerifyReqDto;

public class CaptchaException {
    @Slf4j
    public static class CaptchaVerificationException extends AbstractRestException {
        public CaptchaVerificationException(CaptchaVerifyReqDto reqDto) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.CAPTCHA_RESPONSE_ERROR);
            log.error("Attempt to verified captcha was ended failure. Income details: {}", reqDto);
        }
    }
}
