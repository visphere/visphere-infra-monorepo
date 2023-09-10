/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import pl.moonsphere.lib.i18n.ILocaleExtendableSet;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class AbstractRestException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final ILocaleExtendableSet placeholder;
    private Map<String, Object> variables = new HashMap<>();

    public AbstractRestException(HttpStatus httpStatus, ILocaleExtendableSet placeholder) {
        super(placeholder.getHolder());
        this.httpStatus = httpStatus;
        this.placeholder = placeholder;
    }

    public AbstractRestException(
        HttpStatus httpStatus, ILocaleExtendableSet placeholder, Map<String, Object> variables
    ) {
        this(httpStatus, placeholder);
        this.variables = variables;
    }
}
