/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public abstract class AbstractServerExceptionResDto {
    protected final String timestamp;
    protected final int status;
    protected final String path;
    protected final String method;

    public AbstractServerExceptionResDto(HttpStatus httpStatus, HttpServletRequest req) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        this.timestamp = now.format(DateTimeFormatter.ISO_INSTANT);
        this.status = httpStatus.value();
        this.path = req.getServletPath();
        this.method = req.getMethod();
    }
}
