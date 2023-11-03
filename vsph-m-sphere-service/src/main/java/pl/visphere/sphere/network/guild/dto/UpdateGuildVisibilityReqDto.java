/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGuildVisibilityReqDto {
    @NotNull(message = "vsph.sphere.jpa.guildIsPrivate.notNull")
    private boolean isPrivate;

    @NotNull(message = "vsph.sphere.jpa.guildUnactiveAllPreviousLinks.notNull")
    private boolean unactiveAllPreviousLinks;

    @Override
    public String toString() {
        return "{" +
            "isPrivate=" + isPrivate +
            ", unactiveAllPreviousLinks=" + unactiveAllPreviousLinks +
            '}';
    }
}
