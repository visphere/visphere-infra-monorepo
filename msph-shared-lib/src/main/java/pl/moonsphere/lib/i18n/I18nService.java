/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 *
 *     File name: I18nService.java
 *     Last modified: 9/3/23, 9:21 PM
 *     Project name: moonsphere-infra-monorepo
 *     Module name: msph-shared-lib
 *
 * This project is a part of "MoonSphere" instant messenger system. This system is a part of
 * completing an engineers degree in computer science at Silesian University of Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.moonsphere.lib.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Map;

@RequiredArgsConstructor
public class I18nService {
    private final AbstractI18nConfiguration configuration;

    public String getMessage(ILocaleExtendableSet placeholder) {
        return getMessage(placeholder.getHolder(), Map.of());
    }

    public String getMessage(ILocaleExtendableSet placeholder, Map<String, Object> variables) {
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
