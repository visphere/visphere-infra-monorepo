/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.profilecolor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import pl.visphere.lib.RegexConstant;
import pl.visphere.multimedia.validator.ValidateColorPalette;

@Getter
@Setter
public class UpdateProfileColorReqDto {

    @NotBlank(message = "vsph.multimedia.jpa.color.notBlank")
    @Pattern(regexp = RegexConstant.COLOR_HEX, message = "vsph.multimedia.jpa.color.pattern")
    @ValidateColorPalette(message = "vsph.multimedia.jpa.color.inArray")
    private String color;

    @Override
    public String toString() {
        return "{" +
            "color=" + color +
            '}';
    }
}
