/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

class PasswordMatchValidator implements ConstraintValidator<ValidateMatchingPasswords, IPasswordValidatorModel> {
    @Override
    public boolean isValid(IPasswordValidatorModel model, ConstraintValidatorContext context) {
        if (model.getPassword() == null || model.getConfirmedPassword() == null) {
            return false;
        }
        return Objects.equals(model.getPassword(), model.getConfirmedPassword());
    }
}
