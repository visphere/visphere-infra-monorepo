/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.i18n.I18nService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractBaseExceptionListener {
    private final I18nService i18nService;
    private final Environment environment;

    @ExceptionHandler(AbstractRestException.class)
    ResponseEntity<MessageExceptionResDto> restException(AbstractRestException ex, HttpServletRequest req) {
        final HttpStatus responseStatus = ex.getHttpStatus();
        final String message = i18nService.getMessage(ex.getPlaceholder(), ex.getVariables());
        return new ResponseEntity<>(new MessageExceptionResDto(responseStatus, req, message), responseStatus);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<MessageExceptionResDto> queryParamsException(
        MissingServletRequestParameterException ex,
        HttpServletRequest req
    ) {
        final HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        final String message = i18nService.getMessage(
            LibLocaleSet.MISSING_REQUEST_PARAM_EXCEPTION_MESSAGE,
            Map.of("parameter", String.format("%s:%s", ex.getParameterName(), ex.getParameterType()))
        );
        return new ResponseEntity<>(new MessageExceptionResDto(responseStatus, req, message), responseStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<? extends AbstractServerExceptionResDto> invalidArgumentException(
        MethodArgumentNotValidException ex, HttpServletRequest req
    ) {
        final List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        final HttpStatus reponseStatus = HttpStatus.BAD_REQUEST;
        if (errors.isEmpty()) {
            final ObjectError error = ex.getBindingResult().getAllErrors().get(0);
            return new ResponseEntity<>(new MessageExceptionResDto(reponseStatus, req,
                i18nService.getMessage(error.getDefaultMessage())), reponseStatus);
        }
        final Map<String, String> errorsAsMap = new HashMap<>();
        for (final FieldError error : errors) {
            errorsAsMap.put(error.getField(), i18nService.getMessage(error.getDefaultMessage()));
        }
        return new ResponseEntity<>(new ValidationExceptionResDto(reponseStatus, req, errorsAsMap), reponseStatus);
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<MessageExceptionResDto> authenticationException(HttpServletRequest req, AuthenticationException ex) {
        final HttpStatus responseStatus = HttpStatus.UNAUTHORIZED;
        log.error("Spring security context exception. Cause: '{}'.", ex.getMessage());
        return new ResponseEntity<>(new MessageExceptionResDto(responseStatus, req, ex.getMessage()), responseStatus);
    }

    @ExceptionHandler({ Exception.class })
    ResponseEntity<MessageExceptionResDto> unknowServerException(HttpServletRequest req, Exception ex) {
        final HttpStatus reponseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final String message = i18nService.getMessage(LibLocaleSet.UNKNOW_SERVER_EXCEPTION_MESSAGE);
        log.error("Unexpected issue during server process. Cause: '{}'.", ex.getMessage());
        return new ResponseEntity<>(new MessageExceptionResDto(reponseStatus, req, message), reponseStatus);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<MessageExceptionResDto> maxUploadSizeExceededException(
        HttpServletRequest req,
        MaxUploadSizeExceededException ex
    ) {
        final HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        final String message = i18nService.getMessage(LibLocaleSet.MAX_UPLOADED_FILE_SIZE_EXCEEDED_EXCEPTION_MESSAGE, Map.of(
            "maxSize", environment.getProperty("spring.servlet.multipart.max-file-size", "?")
        ));
        log.error("Multipart file max upload size eceeded. Cause: '{}'.", ex.getMaxUploadSize());
        return new ResponseEntity<>(new MessageExceptionResDto(responseStatus, req, message), responseStatus);
    }
}
