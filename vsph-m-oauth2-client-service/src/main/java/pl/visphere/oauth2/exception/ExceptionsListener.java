/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.visphere.lib.exception.AbstractBaseExceptionListener;
import pl.visphere.lib.i18n.I18nService;

@RestControllerAdvice
class ExceptionsListener extends AbstractBaseExceptionListener {
    ExceptionsListener(I18nService i18nService) {
        super(i18nService);
    }
}
