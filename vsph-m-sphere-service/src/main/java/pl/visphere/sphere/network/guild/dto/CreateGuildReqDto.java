/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pl.visphere.lib.validator.enums.ValidateEnums;
import pl.visphere.sphere.domain.guild.GuildCategory;

@Getter
@Setter
public class CreateGuildReqDto {
    @NotBlank(message = "vsph.sphere.jpa.guildName.notBlank")
    @Size(min = 3, max = 100, message = "vsph.sphere.jpa.guildName.size")
    private String name;

    @NotNull(message = "vsph.sphere.jpa.guildCategory.notNull")
    @ValidateEnums(type = GuildCategory.class, message = "vsph.sphere.jpa.guildCategory.enum")
    private GuildCategory category;

    @NotNull(message = "vsph.sphere.jpa.guildIsPrivate.notNull")
    private boolean isPrivate;

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", category=" + category +
            ", isPrivate=" + isPrivate +
            '}';
    }
}
