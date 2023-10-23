/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import java.util.Map;

public record KafkaNullableResponseWrapper(
    Object payload,
    boolean exOccurred,
    String exPlaceholder,
    int status,
    Map<String, Object> exMessageParams
) {
}
