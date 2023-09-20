/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.apigateway.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum WebFluxLocaleSet {
    UNKNOW_GATEWAY_ERROR("msph.i18n.gateway.unknowError", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE_GATEWAY_ERROR("msph.i18n.gateway.serviceUnavailable", HttpStatus.SERVICE_UNAVAILABLE);

    private final String holder;
    private final HttpStatus httpStatus;

    public static WebFluxLocaleSet getBaseStatusCode(HttpStatus httpStatus) {
        return Arrays.stream(values())
            .filter(set -> set.httpStatus == httpStatus)
            .findFirst()
            .orElse(WebFluxLocaleSet.UNKNOW_GATEWAY_ERROR);
    }
}
