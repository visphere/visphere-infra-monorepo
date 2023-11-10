/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.service.mail;

import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;

public interface MailService {
    void activateAccount(SendTokenEmailReqDto reqDto);
    void newAccount(SendBaseEmailReqDto reqDto);
    void changePassword(SendTokenEmailReqDto reqDto);
    void passwordChanged(SendBaseEmailReqDto reqDto);
    void mfaCode(SendTokenEmailReqDto reqDto);
}
