/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.user;

import java.util.Map;

public record UsersDetailsResDto(
    Map<Long, UserDetails> userDetails
) {
}
