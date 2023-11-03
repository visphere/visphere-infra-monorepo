/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import pl.visphere.lib.validator.enums.ValidateEnums;
import pl.visphere.sphere.domain.guild.GuildCategory;

@Getter
@Setter
public class UpdateGuildCategoryReqDto {
    @NotBlank(message = "vsph.sphere.jpa.guildCategory.notBlank")
    @ValidateEnums(type = GuildCategory.class, message = "vsph.sphere.jpa.guildCategory.enum")
    private GuildCategory category;

    @Override
    public String toString() {
        return "{" +
            "category=" + category +
            '}';
    }
}
