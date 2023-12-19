/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.textchannel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pl.visphere.lib.RegexConstant;

@Getter
@Setter
public class UpdateTextChannelReqDto {

    @NotBlank(message = "vsph.sphere.jpa.textChannelName.notBlank")
    @Size(min = 3, max = 50, message = "vsph.sphere.jpa.textChannelName.size")
    @Pattern(regexp = RegexConstant.TEXT_CHANNEL, message = "vsph.sphere.jpa.textChannelName.pattern")
    private String name;

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            '}';
    }
}
