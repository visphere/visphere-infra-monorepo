/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGuildNameReqDto {
    @NotBlank(message = "vsph.sphere.jpa.guildName.notBlank")
    @Size(min = 3, max = 100, message = "vsph.sphere.jpa.guildName.size")
    private String name;

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            '}';
    }
}
