/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.validator.enums;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumsValidator.class)
@Documented
public @interface ValidateEnums {
    Class<? extends Enum<?>> type();
    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
