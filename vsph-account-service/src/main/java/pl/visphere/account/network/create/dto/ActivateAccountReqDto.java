/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.create.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateAccountReqDto {

    @NotBlank(message = "vsph.lib.jpa.emailAddress.notBlank")
    @Email(message = "vsph.lib.jpa.emailAddress.email")
    private String emailAddress;

    @Override
    public String toString() {
        return "{" +
            "emailAddress=" + emailAddress +
            '}';
    }
}
