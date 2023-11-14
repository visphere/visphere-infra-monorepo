/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pl.visphere.lib.RegexConstant;

@Getter
@Setter
public class UpdateFillDataReqDto {

    @NotBlank(message = "vsph.lib.jpa.username.notBlank")
    @Size(min = 2, max = 30, message = "vsph.lib.jpa.username.size")
    @Pattern(regexp = RegexConstant.USERNAME_REQ, message = "vsph.lib.jpa.username.pattern")
    private String username;

    @NotBlank(message = "vsph.lib.jpa.firstName.notBlank")
    @Size(min = 2, max = 80, message = "vsph.lib.jpa.firstName.size")
    private String firstName;

    @NotBlank(message = "vsph.lib.jpa.lastName.notBlank")
    @Size(min = 2, max = 80, message = "vsph.lib.jpa.lastName.size")
    private String lastName;

    @NotBlank(message = "vsph.lib.jpa.birthDate.notBlank")
    @Pattern(regexp = RegexConstant.BIRTH_DATE_REQ, message = "vsph.lib.jpa.birthDate.pattern")
    private String birthDate;

    @NotNull(message = "vsph.lib.jpa.allowNotifs.notNull")
    private Boolean allowNotifs;

    @Override
    public String toString() {
        return "{" +
            "username='" + username +
            ", firstName='" + firstName +
            ", lastName='" + lastName +
            ", birthDate='" + birthDate +
            ", allowNotifs=" + allowNotifs +
            '}';
    }
}
