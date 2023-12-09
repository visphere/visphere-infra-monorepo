/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.notification;

public interface SendEmailReqDto {
    Long getUserId();
    String getFullName();
    String getEmailAddress();
    String getProfileImageUuid();
}
