/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.participant.dto;

import lombok.Builder;

@Builder
public record BannerMemberDetailsResDto(
    Long id,
    String fullName,
    String username,
    String profileColor,
    String profileImageUrl
) {
}
