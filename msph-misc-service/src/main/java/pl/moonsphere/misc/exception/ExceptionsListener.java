/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.misc.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.moonsphere.lib.exception.AbstractBaseExceptionListener;
import pl.moonsphere.lib.i18n.I18nService;

@RestControllerAdvice
public class ExceptionsListener extends AbstractBaseExceptionListener {
    public ExceptionsListener(I18nService i18nService) {
        super(i18nService);
    }
}
