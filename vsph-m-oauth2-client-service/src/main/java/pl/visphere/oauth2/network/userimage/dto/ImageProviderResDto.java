/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.userimage.dto;

import lombok.Builder;

@Builder
public record ImageProviderResDto(
    String message,
    String resourcePath
) {
}
