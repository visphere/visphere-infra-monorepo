/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecondEmailAddressReqDto implements EmailAddressDto {

    @NotBlank(message = "vsph.auth.jpa.secondEmailAddress.notBlank")
    @Size(max = 100, message = "vsph.auth.jpa.secondEmailAddress.size")
    @Email(message = "vsph.auth.jpa.secondEmailAddress.email")
    private String emailAddress;

    @Override
    public String toString() {
        return "{" +
            "emailAddress=" + emailAddress +
            '}';
    }
}
