/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message.dto;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
public record MessagePayloadResDto(
    Long userId,
    String messageId,
    String fullName,
    String profileImageUrl,
    ZonedDateTime sendDate,
    String message,
    Boolean accountDeleted,
    List<AttachmentFile> attachments
) {
    @Override
    public String toString() {
        return "{" +
            "userId=" + userId +
            ", messageId='" + messageId +
            ", sendDate=" + sendDate +
            ", message='" + message.replace("\n", "\\n") +
            ", attachments=" + attachments +
            '}';
    }
}
