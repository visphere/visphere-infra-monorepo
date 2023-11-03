/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guildlink.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class CreateGuildLinkReqDto {
    @NotNull(message = "vsph.sphere.jpa.guildLinkExpiredAt.notNull")
    private ZonedDateTime expiredAt;

    @Override
    public String toString() {
        return "{" +
            "expiredAt=" + expiredAt +
            '}';
    }
}
