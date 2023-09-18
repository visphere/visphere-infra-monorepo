/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.password_refresh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import pl.moonsphere.lib.RegexConstant;
import pl.moonsphere.lib.validator.IPasswordValidatorModel;
import pl.moonsphere.lib.validator.ValidateMatchingPasswords;

@Getter
@Setter
@ValidateMatchingPasswords(message = "msph.lib.jpa.passwords.notMatch")
public class ChangeReqDto implements IPasswordValidatorModel {

    @NotBlank(message = "msph.lib.jpa.password.notBlank")
    @Pattern(regexp = RegexConstant.PASSWORD_REQ, message = "msph.lib.jpa.password.pattern")
    private String newPassword;

    @NotBlank(message = "msph.lib.jpa.confirmedPassword.notBlank")
    private String confirmedNewPassword;

    @Override
    public String getPassword() {
        return newPassword;
    }

    @Override
    public String getConfirmedPassword() {
        return confirmedNewPassword;
    }

    @Override
    public String toString() {
        return "{" +
            "newPassword=" + newPassword +
            ", confirmedNewPassword=" + confirmedNewPassword +
            '}';
    }
}
