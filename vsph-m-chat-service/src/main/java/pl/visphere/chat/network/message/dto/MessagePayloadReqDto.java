/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message.dto;

public record MessagePayloadReqDto(
    String fullName,
    String profileImageUrl,
    String message
) {
}
