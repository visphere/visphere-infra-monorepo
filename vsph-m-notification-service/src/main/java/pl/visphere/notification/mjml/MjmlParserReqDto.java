/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.mjml;

import lombok.Builder;

@Builder
public record MjmlParserReqDto(
    String rawData
) {
}
