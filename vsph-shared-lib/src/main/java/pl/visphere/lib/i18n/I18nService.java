/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Map;

@RequiredArgsConstructor
public class I18nService {
    private final AbstractI18nConfiguration configuration;

    public String getMessage(LocaleExtendableSet placeholder) {
        return getMessage(placeholder.getHolder(), Map.of());
    }

    public String getMessage(LocaleExtendableSet placeholder, Map<String, Object> variables) {
        return getMessage(placeholder.getHolder(), variables);
    }

    public String getMessage(String placeholder) {
        return getMessage(placeholder, Map.of());
    }

    public String getMessage(String placeholder, Map<String, Object> variables) {
        try {
            String text = configuration.messageSource().getMessage(placeholder, null, LocaleContextHolder.getLocale());
            if (text.isBlank()) {
                return placeholder;
            }
            for (final Map.Entry<String, Object> variable : variables.entrySet()) {
                text = text.replace("{{" + variable.getKey() + "}}", String.valueOf(variable.getValue()));
            }
            return text;
        } catch (NoSuchMessageException ignored) {
            return placeholder;
        }
    }
}
