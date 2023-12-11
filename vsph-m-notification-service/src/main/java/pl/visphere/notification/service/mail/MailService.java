/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.service.mail;

import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendStateEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;

public interface MailService {
    void activateAccount(SendTokenEmailReqDto reqDto);
    void newAccount(SendBaseEmailReqDto reqDto);
    void changePassword(SendTokenEmailReqDto reqDto);
    void passwordChanged(SendBaseEmailReqDto reqDto);
    void mfaCode(SendTokenEmailReqDto reqDto);
    void updatedMfaState(SendStateEmailReqDto reqDto);
    void resetMfaState(SendBaseEmailReqDto reqDto);
    void reqChangeEmail(SendTokenEmailReqDto reqDto);
    void reqChangeSecondEmail(SendTokenEmailReqDto reqDto);
    void changedEmail(SendBaseEmailReqDto reqDto);
    void changedSecondEmail(SendBaseEmailReqDto reqDto);
    void removedSecondEmail(SendBaseEmailReqDto reqDto);
    void enabledAccount(SendBaseEmailReqDto reqDto);
    void disabledAccount(SendBaseEmailReqDto reqDto);
    void deletedAccount(SendBaseEmailReqDto reqDto);
}
