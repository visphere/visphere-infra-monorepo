/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.apigateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
class WebfluxExceptionResDto {
    private final String timestamp;
    private final int status;
    private final String path;
    private final String method;
    private final String message;

    WebfluxExceptionResDto(HttpStatus httpStatus, String path, String method, String message) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        this.timestamp = now.format(DateTimeFormatter.ISO_INSTANT);
        this.status = httpStatus.value();
        this.path = path;
        this.method = method;
        this.message = message;
    }
}
