/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.create.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import pl.moonsphere.lib.RegexConstant;
import pl.moonsphere.lib.validator.IPasswordValidatorModel;
import pl.moonsphere.lib.validator.ValidateMatchingPasswords;

@Getter
@Setter
@ValidateMatchingPasswords(message = "msph.lib.jpa.passwords.notMatch")
public class CreateAccountReqDto implements IPasswordValidatorModel {

    @NotBlank(message = "msph.account.jpa.username.notBlank")
    @Size(min = 2, max = 30, message = "msph.account.jpa.username.size")
    @Pattern(regexp = RegexConstant.USERNAME_REQ, message = "msph.account.jpa.username.pattern")
    private String username;

    @NotBlank(message = "msph.lib.jpa.emailAddress.notBlank")
    @Size(max = 100, message = "msph.lib.jpa.emailAddress.size")
    @Email(message = "msph.lib.jpa.emailAddress.email")
    private String emailAddress;

    @NotBlank(message = "msph.lib.jpa.password.notBlank")
    @Pattern(regexp = RegexConstant.PASSWORD_REQ, message = "msph.lib.jpa.password.pattern")
    private String password;

    @NotBlank(message = "msph.lib.jpa.confirmedPassword.notBlank")
    private String confirmedPassword;

    @NotBlank(message = "msph.account.jpa.birthDate.notBlank")
    @Pattern(regexp = RegexConstant.BIRTH_DATE_REQ, message = "msph.account.jpa.birthDate.pattern")
    private String birthDate;

    @NotBlank(message = "msph.account.jpa.firstName.notBlank")
    @Size(min = 2, max = 80, message = "msph.account.jpa.firstName.size")
    private String firstName;

    @NotBlank(message = "msph.account.jpa.lastName.notBlank")
    @Size(min = 2, max = 80, message = "msph.account.jpa.lastName.size")
    private String lastName;

    @NotNull(message = "msph.account.jpa.secondEmailAddress.notNull")
    @Email(message = "msph.account.jpa.secondEmailAddress.email")
    private String secondEmailAddress;

    @NotNull(message = "msph.account.jpa.allowNotifs.notNull")
    private Boolean allowNotifs;

    @NotNull(message = "msph.account.jpa.enableMfa.notNull")
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
