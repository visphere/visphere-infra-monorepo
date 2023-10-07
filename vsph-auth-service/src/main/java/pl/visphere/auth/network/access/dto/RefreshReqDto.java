/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.access.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;

@Getter
@Setter
public class RefreshReqDto {

    @NotBlank(message = "vsph.auth.jpa.expiredAccessToken.notBlank")
    public String expiredAccessToken;

    @NotBlank(message = "vsph.auth.jpa.refreshToken.notBlank")
    @UUID(message = "vsph.auth.jpa.refreshToken.uuid")
    public String refreshToken;

    @Override
    public String toString() {
        return "{" +
            "expiredAccessToken=" + expiredAccessToken +
            ", refreshToken=" + refreshToken +
            '}';
    }
}
