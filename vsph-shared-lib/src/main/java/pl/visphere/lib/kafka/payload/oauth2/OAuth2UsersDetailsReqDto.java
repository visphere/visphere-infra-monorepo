/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.oauth2;

import java.util.List;

public record OAuth2UsersDetailsReqDto(
    List<Long> userIds
) {
}
