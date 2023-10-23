/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.i18n.LocaleExtendableSet;
import pl.visphere.lib.kafka.KafkaLocaleTransporter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class AbstractRestException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final LocaleExtendableSet placeholder;
    private Map<String, Object> variables = new HashMap<>();

    public AbstractRestException(HttpStatus httpStatus, LocaleExtendableSet placeholder) {
        super(placeholder.getHolder());
        this.httpStatus = httpStatus;
        this.placeholder = placeholder;
    }

    public AbstractRestException(HttpStatus httpStatus, String placeholder, Map<String, Object> variables) {
        super(placeholder);
        this.httpStatus = httpStatus;
        this.placeholder = new KafkaLocaleTransporter(placeholder);
        this.variables = variables;
    }

    public AbstractRestException(
        HttpStatus httpStatus, LocaleExtendableSet placeholder, Map<String, Object> variables
    ) {
        this(httpStatus, placeholder);
        this.variables = variables;
    }
}
