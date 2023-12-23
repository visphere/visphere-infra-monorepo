/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPasswordReqDto {

    @NotBlank(message = "vsph.lib.jpa.usernameOrEmailAddress.notBlank")
    private String usernameOrEmailAddress;

    @NotBlank(message = "vsph.lib.jpa.password.notBlank")
    private String password;

    @Override
    public String toString() {
        return "{" +
            "usernameOrEmailAddress=" + usernameOrEmailAddress +
            ", password=" + password +
            '}';
    }
}
