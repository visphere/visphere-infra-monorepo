/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.apigateway.i18n;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.LocaleContextResolver;

import java.util.Locale;

@Component
@RequiredArgsConstructor
class I18nWebfluxHeaderLocaleResolver implements LocaleContextResolver {
    private final I18nProperties i18nProperties;

    @Override
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
        final String language = exchange.getRequest().getHeaders().getFirst(HttpHeaders.ACCEPT_LANGUAGE);
        Locale locale = Locale.forLanguageTag(i18nProperties.getDefaultLanguage());
        if (StringUtils.isNotEmpty(language)) {
            locale = Locale.forLanguageTag(language);
        }
        return new SimpleLocaleContext(locale);
    }

    @Override
    public void setLocaleContext(ServerWebExchange exchange, LocaleContext localeContext) {
        throw new UnsupportedOperationException(
            "Cannot change HTTP accept header - use a different locale context resolution strategy");
    }
}
