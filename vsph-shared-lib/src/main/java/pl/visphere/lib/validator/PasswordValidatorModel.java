/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.validator;

public interface PasswordValidatorModel {
    String getPassword();
    String getConfirmedPassword();
}
