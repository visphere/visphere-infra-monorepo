/*
 * Copyright (c) 2024 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.chat;

import java.util.List;

public record DeleteUserMessagesReqDto(
    Long userId,
    List<Long> textChannelIds
) {
}
