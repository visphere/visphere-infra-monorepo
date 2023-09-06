/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 *
 *     File name: CaptchaException.java
 *     Last modified: 9/4/23, 12:25 PM
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
