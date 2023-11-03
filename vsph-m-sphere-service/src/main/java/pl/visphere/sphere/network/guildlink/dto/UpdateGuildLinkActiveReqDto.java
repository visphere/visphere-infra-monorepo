/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guildlink.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGuildLinkActiveReqDto {
    @NotNull(message = "vsph.sphere.jpa.guildLinkIsActive.notNull")
    private boolean isActive;

    @Override
    public String toString() {
        return "{" +
            "isActive=" + isActive +
            '}';
    }
}
