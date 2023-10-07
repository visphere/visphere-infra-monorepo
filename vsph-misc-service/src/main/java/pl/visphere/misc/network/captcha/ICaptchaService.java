/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.misc.network.captcha;

import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.misc.network.captcha.dto.CaptchaVerifyReqDto;

interface ICaptchaService {
    BaseMessageResDto verify(CaptchaVerifyReqDto reqDto);
}
