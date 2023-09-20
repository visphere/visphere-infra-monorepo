/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.apigateway.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class I18nWebfluxService {
    private final I18nWebfluxConfig config;
    private final I18nWebfluxHeaderLocaleResolver resolver;

    public String getMessageFromStatus(HttpStatus httpStatus, ServerWebExchange exchange) {
        final String placeholder = WebFluxLocaleSet.getBaseStatusCode(httpStatus).getHolder();
        final LocaleContext localeContext = resolver.resolveLocaleContext(exchange);
        return getMessageSource().getMessage(placeholder, null, Objects.requireNonNull(localeContext.getLocale()));
    }

    private MessageSource getMessageSource() {
        return config.messageSource();
    }
}
