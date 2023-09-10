/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.misc.network.captcha.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptchaVerifyReqDto {

    @NotBlank(message = "msph.captcha.jpa.response.notBlank")
    private String response;

    @NotBlank(message = "msph.captcha.jpa.remoteIp.notBlank")
    private String remoteIp;

    @NotBlank(message = "msph.captcha.jpa.siteKey.notBlank")
    private String siteKey;

    @Override
    public String toString() {
        return '{' +
            "response=" + response +
            ", remoteIp=" + remoteIp +
            ", siteKey=" + siteKey +
            '}';
    }
}
