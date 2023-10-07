/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.apigateway.exception;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import pl.visphere.apigateway.i18n.I18nWebfluxService;

import java.util.Map;

@Component
class GlobalErrorAttributes extends DefaultErrorAttributes {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final I18nWebfluxService i18nWebfluxService;

    GlobalErrorAttributes(I18nWebfluxService i18nWebfluxService) {
        this.i18nWebfluxService = i18nWebfluxService;
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest req, ErrorAttributeOptions options) {
        final Map<String, Object> primaryAttributes = super.getErrorAttributes(req, options);
        final HttpStatus httpStatus = parseStatus(primaryAttributes);
        final String message = i18nWebfluxService.getMessageFromStatus(httpStatus, req.exchange());
        final var resDto = new WebfluxExceptionResDto(httpStatus, req.path(), req.method().name(), message);
        return objectMapper.convertValue(resDto, new TypeReference<>() {});
    }

    private HttpStatus parseStatus(Map<String, Object> attributes) {
        final String status = ConvertUtils.convert(attributes.getOrDefault("status", 500));
        return HttpStatus.resolve(NumberUtils.toInt(status));
    }
}
