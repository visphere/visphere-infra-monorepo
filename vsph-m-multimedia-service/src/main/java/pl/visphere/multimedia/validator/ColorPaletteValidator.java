/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.visphere.multimedia.processing.ImageProperties;

@Component
@RequiredArgsConstructor
class ColorPaletteValidator implements ConstraintValidator<ValidateColorPalette, String> {
    private final ImageProperties imageProperties;

    @Override
    public boolean isValid(String color, ConstraintValidatorContext context) {
        if (color == null) {
            return false;
        }
        return imageProperties.getColors().stream().anyMatch(c -> c.equals(color));
    }
}
