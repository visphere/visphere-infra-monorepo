/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.kafka.KafkaNullableResponseWrapper;

@Slf4j
public class GenericRestException extends AbstractRestException {
    public GenericRestException(KafkaNullableResponseWrapper wrapper) {
        super(HttpStatus.valueOf(wrapper.status()), wrapper.exPlaceholder(), wrapper.exMessageParams());
    }

    public GenericRestException(String message, Object... args) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, LibLocaleSet.UNKNOW_SERVER_EXCEPTION_MESSAGE);
        log.error(message, args);
    }
}
