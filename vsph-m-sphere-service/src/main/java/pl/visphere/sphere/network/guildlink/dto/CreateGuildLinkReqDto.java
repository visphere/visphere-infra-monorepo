/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guildlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pl.visphere.lib.validator.enums.ValidateEnums;
import pl.visphere.sphere.network.guildlink.ExpiredAfter;

@Getter
@Setter
public class CreateGuildLinkReqDto {

    @NotBlank(message = "vsph.sphere.jpa.guildLinkName.notBlank")
    @Size(min = 3, max = 100, message = "vsph.sphere.jpa.guildLinkName.size")
    private String name;

    @NotNull(message = "vsph.sphere.jpa.expiredAfter.notNull")
    @ValidateEnums(type = ExpiredAfter.class, message = "vsph.sphere.jpa.expiredAfter.enum")
    private ExpiredAfter expiredAfter;

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", expiredAfter=" + expiredAfter +
            '}';
    }
}
