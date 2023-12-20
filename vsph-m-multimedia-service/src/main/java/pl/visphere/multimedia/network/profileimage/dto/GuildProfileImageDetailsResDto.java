/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.profileimage.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record GuildProfileImageDetailsResDto(
    String guildName,
    LocalDate createdDate,
    String profileColor,
    String profileImageUrl,
    String imageType
) {
}
