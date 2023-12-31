/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.oauth2;

import java.util.Map;

public record OAuth2UsersDetailsResDto(
    Map<Long, OAuth2UsersDetails> oauth2UsersImages
) {
}
