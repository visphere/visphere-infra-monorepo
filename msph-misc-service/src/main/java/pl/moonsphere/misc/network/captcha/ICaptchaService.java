/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.misc.network.captcha;

import pl.moonsphere.lib.BaseMessageResDto;
import pl.moonsphere.misc.network.captcha.dto.CaptchaVerifyReqDto;

public interface ICaptchaService {
    BaseMessageResDto verify(CaptchaVerifyReqDto reqDto);
}
