/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordReqDto {

    @NotNull(message = "vsph.lib.jpa.password.notNull")
    private String password;

    @NotNull(message = "vsph.lib.jpa.mfaCode.notNull")
    private String mfaCode;

    @Override
    public String toString() {
        return "{" +
            "password=" + password +
            ", mfaCode=" + mfaCode +
            '}';
    }
}
