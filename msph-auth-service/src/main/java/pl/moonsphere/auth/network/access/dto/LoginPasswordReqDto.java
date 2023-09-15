/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.access.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPasswordReqDto {

    @NotBlank(message = "msph.lib.jpa.usernameOrEmailAddress.notBlank")
    private String usernameOrEmailAddress;

    @NotBlank(message = "msph.lib.jpa.password.notBlank")
    private String password;

    @Override
    public String toString() {
        return "{" +
            "usernameOrEmailAddress=" + usernameOrEmailAddress +
            ", password=" + password +
            '}';
    }
}
