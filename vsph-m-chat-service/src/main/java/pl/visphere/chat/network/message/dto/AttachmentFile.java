/*
 * Copyright (c) 2024 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.network.message.dto;

import lombok.Builder;

@Builder
public record AttachmentFile(
    String path,
    String originalName,
    String mimeType
) {
}
