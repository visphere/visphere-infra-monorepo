/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message.dto;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Builder
public record MessagesResDto(
    List<MessagePayloadResDto> messages,
    String paginationState,
    Boolean paginationEnd
) {
    public MessagesResDto(boolean paginationEnd) {
        this(List.of(), StringUtils.EMPTY, paginationEnd);
    }
}
