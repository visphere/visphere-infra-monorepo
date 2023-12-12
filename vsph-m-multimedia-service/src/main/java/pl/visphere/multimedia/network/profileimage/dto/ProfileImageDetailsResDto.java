/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.profileimage.dto;

import lombok.Builder;

@Builder
public record ProfileImageDetailsResDto(
    String imageType
) {
}
