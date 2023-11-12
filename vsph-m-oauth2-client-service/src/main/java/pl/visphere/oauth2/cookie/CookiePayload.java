/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.cookie;

import lombok.Builder;

@Builder
public record CookiePayload(
    ConfigurableCookie cookie,
    String value,
    int maxAge
) {
}
