/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.mail.mjml;

import lombok.Builder;
import pl.visphere.notification.hbs.HbsTemplate;

import java.util.List;

@Builder
public record MjmlParserReqDto(
    String rawData,
    List<String> sendToEmails,
    String messageUuid,
    HbsTemplate hbsTemplate
) {
}
