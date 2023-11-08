/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtClaim {
    USER_EMAIL("user_email"),
    USER_ID("user_id"),
    MESSAGE_UUID("message_uuid"),
    ;

    private final String claim;
}
