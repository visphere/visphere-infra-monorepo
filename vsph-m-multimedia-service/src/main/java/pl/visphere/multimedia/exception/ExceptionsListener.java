/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import pl.visphere.lib.exception.AbstractBaseExceptionListener;
import pl.visphere.lib.exception.MessageExceptionResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.multimedia.i18n.LocaleSet;

import java.util.Map;

@Slf4j
@RestControllerAdvice
class ExceptionsListener extends AbstractBaseExceptionListener {
    private final I18nService i18nService;
    private final Environment environment;

    ExceptionsListener(I18nService i18nService, Environment environment) {
        super(i18nService);
        this.i18nService = i18nService;
        this.environment = environment;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<MessageExceptionResDto> maxUploadSizeExceededException(
        HttpServletRequest req,
        MaxUploadSizeExceededException ex
    ) {
        final HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        final String message = i18nService.getMessage(LocaleSet.MAX_UPLOADED_FILE_SIZE_EXCEEDED_EXCEPTION_MESSAGE, Map.of(
            "maxSize", environment.getProperty("spring.servlet.multipart.max-file-size", "?")
        ));
        log.error("Multipart file max upload size eceeded. Cause: '{}'.", ex.getMaxUploadSize());
        return new ResponseEntity<>(new MessageExceptionResDto(responseStatus, req, message), responseStatus);
    }
}
