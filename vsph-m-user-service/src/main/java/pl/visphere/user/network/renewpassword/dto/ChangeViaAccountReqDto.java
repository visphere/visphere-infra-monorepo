/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.renewpassword.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import pl.visphere.lib.RegexConstant;
import pl.visphere.lib.validator.password.PasswordValidatorModel;
import pl.visphere.lib.validator.password.ValidateMatchingPasswords;

@Getter
@Setter
@ValidateMatchingPasswords(message = "vsph.lib.jpa.passwords.notMatch")
public class ChangeViaAccountReqDto implements PasswordValidatorModel {

    @NotBlank(message = "vsph.user.jpa.oldPassword.notBlank")
    private String oldPassword;

    @NotBlank(message = "vsph.lib.jpa.password.notBlank")
    @Pattern(regexp = RegexConstant.PASSWORD_REQ, message = "vsph.lib.jpa.password.pattern")
    private String newPassword;

    @NotBlank(message = "vsph.lib.jpa.confirmedPassword.notBlank")
    private String confirmedNewPassword;

    @NotNull(message = "vsph.lib.jpa.logoutFromAll.notNull")
    private boolean logoutFromAll;

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
            "oldPassword=" + oldPassword +
            ", newPassword=" + newPassword +
            ", confirmedNewPassword=" + confirmedNewPassword +
            '}';
    }
}
