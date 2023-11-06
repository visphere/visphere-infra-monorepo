/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.mail;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record MailPayloadDto(
    Set<String> sendTo,
    String title,
    List<MailResourceDto> inlineResources,
    List<MailResourceDto> attachments,
    String htmlContent
) {
    @Override
    public String toString() {
        return "{" +
            "sendTo=" + sendTo +
            ", title=" + title +
            ", inlineResources=" + inlineResources +
            ", attachments=" + attachments +
            '}';
    }
}
