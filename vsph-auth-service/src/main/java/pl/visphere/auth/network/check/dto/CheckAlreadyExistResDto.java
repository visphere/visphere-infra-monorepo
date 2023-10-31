/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.check.dto;

import lombok.Builder;

@Builder
public record CheckAlreadyExistResDto(
    boolean alreadyExist
) {
}
