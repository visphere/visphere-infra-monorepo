/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.create.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import pl.visphere.lib.RegexConstant;
import pl.visphere.lib.validator.IPasswordValidatorModel;
import pl.visphere.lib.validator.ValidateMatchingPasswords;

@Getter
@Setter
@ValidateMatchingPasswords(message = "vsph.lib.jpa.passwords.notMatch")
public class CreateAccountReqDto implements IPasswordValidatorModel {

    @NotBlank(message = "vsph.account.jpa.username.notBlank")
    @Size(min = 2, max = 30, message = "vsph.account.jpa.username.size")
    @Pattern(regexp = RegexConstant.USERNAME_REQ, message = "vsph.account.jpa.username.pattern")
    private String username;

    @NotBlank(message = "vsph.lib.jpa.emailAddress.notBlank")
    @Size(max = 100, message = "vsph.lib.jpa.emailAddress.size")
    @Email(message = "vsph.lib.jpa.emailAddress.email")
    private String emailAddress;

    @NotBlank(message = "vsph.lib.jpa.password.notBlank")
    @Pattern(regexp = RegexConstant.PASSWORD_REQ, message = "vsph.lib.jpa.password.pattern")
    private String password;

    @NotBlank(message = "vsph.lib.jpa.confirmedPassword.notBlank")
    private String confirmedPassword;

    @NotBlank(message = "vsph.account.jpa.birthDate.notBlank")
    @Pattern(regexp = RegexConstant.BIRTH_DATE_REQ, message = "vsph.account.jpa.birthDate.pattern")
    private String birthDate;

    @NotBlank(message = "vsph.account.jpa.firstName.notBlank")
    @Size(min = 2, max = 80, message = "vsph.account.jpa.firstName.size")
    private String firstName;

    @NotBlank(message = "vsph.account.jpa.lastName.notBlank")
    @Size(min = 2, max = 80, message = "vsph.account.jpa.lastName.size")
    private String lastName;

    @NotNull(message = "vsph.account.jpa.secondEmailAddress.notNull")
    @Email(message = "vsph.account.jpa.secondEmailAddress.email")
    private String secondEmailAddress;

    @NotNull(message = "vsph.account.jpa.allowNotifs.notNull")
    private Boolean allowNotifs;

    @NotNull(message = "vsph.account.jpa.enableMfa.notNull")
    private Boolean enabledMfa;

    @Override
    public String toString() {
        return "{" +
            "username=" + username +
            ", emailAddress=" + emailAddress +
            ", password=" + password +
            ", confirmedPassword=" + confirmedPassword +
            ", birthDate=" + birthDate +
            ", firstName=" + firstName +
            ", lastName=" + lastName +
            ", secondEmailAddress=" + secondEmailAddress +
            ", allowNotifs=" + allowNotifs +
            ", enabledMfa=" + enabledMfa +
            '}';
    }
}
