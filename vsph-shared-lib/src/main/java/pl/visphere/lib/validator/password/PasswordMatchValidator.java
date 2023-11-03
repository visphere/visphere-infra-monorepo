/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.validator.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

class PasswordMatchValidator implements ConstraintValidator<ValidateMatchingPasswords, PasswordValidatorModel> {
    @Override
    public boolean isValid(PasswordValidatorModel model, ConstraintValidatorContext context) {
        if (model.getPassword() == null || model.getConfirmedPassword() == null) {
            return false;
        }
        return Objects.equals(model.getPassword(), model.getConfirmedPassword());
    }
}
