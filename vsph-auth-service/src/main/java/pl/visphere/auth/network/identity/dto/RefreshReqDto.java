/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshReqDto {

    @NotBlank(message = "vsph.auth.jpa.expiredAccessToken.notBlank")
    public String expiredAccessToken;

    @Override
    public String toString() {
        return "{" +
            "expiredAccessToken=" + expiredAccessToken +
            '}';
    }
}
