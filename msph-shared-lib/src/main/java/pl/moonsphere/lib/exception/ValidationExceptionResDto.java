/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ValidationExceptionResDto extends AbstractServerExceptionResDto {
    private final Map<String, String> errors;

    public ValidationExceptionResDto(HttpStatus httpStatus, HttpServletRequest req, Map<String, String> errors) {
        super(httpStatus, req);
        this.errors = errors;
    }
}
