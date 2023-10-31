/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.check.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;
import pl.visphere.lib.RegexConstant;

@Getter
@Setter
public class MyAccountReqDto {

    @NotBlank(message = "vsph.auth.jpa.accountId.notBlank")
    @UUID(message = "vsph.auth.jpa.accountId.uuid")
    private String accountId;

    @NotBlank(message = "vsph.lib.jpa.usernameOrEmailAddress.notBlank")
    @Size(max = 100, message = "vsph.lib.jpa.usernameOrEmailAddress.size")
    @Pattern(regexp = RegexConstant.USERNAME_OR_EMAIL_REQ, message = "vsph.lib.jpa.usernameOrEmailAddress.pattern")
    private String usernameOrEmailAddress;

    @NotNull(message = "vsph.auth.jpa.isVerified.notNull")
    private Boolean isVerified;

    @Override
    public String toString() {
        return "{" +
            "accountId=" + accountId +
            ", usernameOrEmailAddress=" + usernameOrEmailAddress +
            ", isVerified=" + isVerified +
            '}';
    }
}
