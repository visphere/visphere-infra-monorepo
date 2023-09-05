/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 *
 *     File name: CustomHeaderLocaleResolver.java
 *     Last modified: 9/3/23, 8:30 PM
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

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

public class CustomHeaderLocaleResolver extends AcceptHeaderLocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest req) {
        final String acceptLang = req.getHeader("Accept-Language");
        if (StringUtils.isEmpty(acceptLang)) {
            return getDefaultLocale();
        }
        final List<Locale.LanguageRange> languageRanges = Locale.LanguageRange.parse(acceptLang);
        final Locale currentLocale = Locale.lookup(languageRanges, getSupportedLocales());
        LocaleContextHolder.setLocale(currentLocale);
        return currentLocale;
    }
}
