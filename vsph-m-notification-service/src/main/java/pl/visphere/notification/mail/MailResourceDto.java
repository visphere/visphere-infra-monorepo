/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.mail;

import lombok.Builder;

import java.io.File;

@Builder
public record MailResourceDto(
    String name,
    File file
) {
}
