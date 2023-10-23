/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.jwt;

import io.jsonwebtoken.Claims;
import lombok.Builder;

@Builder
public record JwtValidateState(
    JwtState state,
    Claims claims
) {
}
