/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.network.mirror.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MirrorMailReqDto {

    @NotBlank(message = "vsph.notification.jpa.token.notBlank")
    private String token;

    @Override
    public String toString() {
        return "{" +
            "token=" + token +
            '}';
    }
}
