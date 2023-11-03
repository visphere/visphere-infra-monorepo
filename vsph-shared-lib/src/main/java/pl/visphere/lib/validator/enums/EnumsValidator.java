/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.validator.enums;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

class EnumsValidator implements ConstraintValidator<ValidateEnums, Enum<?>> {
    private Set<String> values;

    @Override
    public void initialize(ValidateEnums constraintAnnotation) {
        values = Arrays.stream(constraintAnnotation.type().getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return values.contains(value.name());
    }
}
