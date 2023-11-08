/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.network.mirror.dto;

import lombok.Builder;

@Builder
public record MirrorMailResDto(
    String rawHtml
) {
}
