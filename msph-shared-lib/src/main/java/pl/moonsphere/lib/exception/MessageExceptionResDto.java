/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MessageExceptionResDto extends AbstractServerExceptionResDto {
    private final String message;

    public MessageExceptionResDto(HttpStatus httpStatus, HttpServletRequest req, String message) {
        super(httpStatus, req);
        this.message = message;
    }
}
