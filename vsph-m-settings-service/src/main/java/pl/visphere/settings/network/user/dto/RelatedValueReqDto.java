/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.network.user.dto;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatedValueReqDto {

    @Nullable
    private String relatedValue;

    @Override
    public String toString() {
        return "{" +
            "relatedValue=" + relatedValue +
            '}';
    }
}
