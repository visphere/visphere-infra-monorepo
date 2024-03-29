/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.renewpassword.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pl.visphere.lib.RegexConstant;

@Getter
@Setter
public class AttemptReqDto {

    @NotBlank(message = "vsph.lib.jpa.usernameOrEmailAddress.notBlank")
    @Size(min = 2, max = 100, message = "vsph.lib.jpa.usernameOrEmailAddress.size")
    @Pattern(regexp = RegexConstant.USERNAME_OR_EMAIL_REQ, message = "vsph.lib.jpa.usernameOrEmailAddress.pattern")
    private String usernameOrEmailAddress;

    @Override
    public String toString() {
        return "{" +
            "usernameOrEmailAddress=" + usernameOrEmailAddress +
            '}';
    }
}
