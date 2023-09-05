/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 *
 *     File name: AbstractBaseExceptionListener.java
 *     Last modified: 9/3/23, 7:04 PM
 *     Project name: moonsphere-infra-monorepo
 *     Module name: msph-shared-lib
 *
 * This project is a part of "MoonSphere" instant messenger system. This system is a part of
 * completing an engineers degree in computer science at Silesian University of Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */
package pl.moonsphere.lib.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.moonsphere.lib.i18n.I18nService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractBaseExceptionListener {
    private final I18nService i18nService;

    @ExceptionHandler(AbstractRestException.class)
    public ResponseEntity<MessageExceptionResDto> restException(AbstractRestException ex, HttpServletRequest req) {
        final HttpStatus responseStatus = ex.getHttpStatus();
        final String message = i18nService.getMessage(ex.getPlaceholder(), ex.getVariables());
        return new ResponseEntity<>(new MessageExceptionResDto(responseStatus, req, message), responseStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<? extends AbstractServerExceptionResDto> invalidArgumentException(
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
}
