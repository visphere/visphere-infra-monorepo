/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.oauth2;

import lombok.Builder;

@Builder
public record OAuth2UsersDetails(
    String profileImageUrl,
    boolean imageFromLocal
) {
}
