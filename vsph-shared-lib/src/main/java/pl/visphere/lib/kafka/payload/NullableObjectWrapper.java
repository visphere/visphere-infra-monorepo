/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload;

import pl.visphere.lib.kafka.ResponseObject;

public record NullableObjectWrapper<T>(
    ResponseObject response,
    T content
) {
}
