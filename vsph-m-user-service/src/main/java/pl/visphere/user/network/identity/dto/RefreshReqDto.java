/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;

@Getter
@Setter
public class RefreshReqDto {

    @NotBlank(message = "vsph.user.jpa.expiredAccessToken.notBlank")
    private String expiredAccessToken;

    @NotBlank(message = "vsph.user.jpa.refreshToken.notBlank")
    @UUID(message = "vsph.user.jpa.refreshToken.uuid")
    private String refreshToken;

    @Override
    public String toString() {
        return "{" +
            "expiredAccessToken=" + expiredAccessToken +
            ", refreshToken=" + refreshToken +
            '}';
    }
}
