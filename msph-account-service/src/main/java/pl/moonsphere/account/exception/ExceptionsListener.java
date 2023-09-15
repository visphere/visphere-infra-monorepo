/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.moonsphere.lib.exception.AbstractBaseExceptionListener;
import pl.moonsphere.lib.i18n.I18nService;

@RestControllerAdvice
class ExceptionsListener extends AbstractBaseExceptionListener {
    ExceptionsListener(I18nService i18nService) {
        super(i18nService);
    }
}
