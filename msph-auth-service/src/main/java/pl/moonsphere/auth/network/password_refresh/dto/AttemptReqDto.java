/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.password_refresh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pl.moonsphere.lib.RegexConstant;

@Getter
@Setter
public class AttemptReqDto {

    @NotBlank(message = "msph.lib.jpa.usernameOrEmailAddress.notBlank")
    @Size(min = 2, max = 100, message = "msph.lib.jpa.usernameOrEmailAddress.size")
    @Pattern(regexp = RegexConstant.USERNAME_OR_EMAIL_REQ, message = "msph.lib.jpa.usernameOrEmailAddress.pattern")
    private String usernameOrEmailAddress;

    @Override
    public String toString() {
        return "{" +
            "usernameOrEmailAddress=" + usernameOrEmailAddress +
            '}';
    }
}
