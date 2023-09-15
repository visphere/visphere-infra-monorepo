/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.create.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateAccountReqDto {

    @NotBlank(message = "msph.lib.jpa.emailAddress.notBlank")
    @Email(message = "msph.lib.jpa.emailAddress.email")
    private String emailAddress;

    @Override
    public String toString() {
        return "{" +
            "emailAddress=" + emailAddress +
            '}';
    }
}
