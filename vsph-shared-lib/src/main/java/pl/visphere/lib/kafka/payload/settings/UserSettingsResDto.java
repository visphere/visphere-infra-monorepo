/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.settings;

import lombok.Builder;

@Builder
public record UserSettingsResDto(
    String theme,
    String lang
) {
}
