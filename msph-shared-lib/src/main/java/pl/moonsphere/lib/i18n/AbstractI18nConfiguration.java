/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 *
 *     File name: AbstractI18nConfiguration.java
 *     Last modified: 9/3/23, 8:49 PM
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
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

@RequiredArgsConstructor
public abstract class AbstractI18nConfiguration {
    private final Locale defaultLocale;
    private final Locale[] availableLocales;
    private final String[] localeBundles;

    public AbstractI18nConfiguration() {
        this.defaultLocale = AppLocale.PL;
        this.availableLocales = new Locale[]{AppLocale.PL, Locale.US};
        this.localeBundles = new String[]{"i18n/messages"};
    }

    @Primary
    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.addBasenames(createLocaleBundlePaths());
        source.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        return source;
    }

    @Bean
    public LocaleResolver localeResolver() {
        final AcceptHeaderLocaleResolver resolver = new CustomHeaderLocaleResolver();
        resolver.setDefaultLocale(defaultLocale);
        resolver.setSupportedLocales(Arrays.asList(availableLocales));
        return resolver;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    private String[] createLocaleBundlePaths() {
        final String[] basenames = new String[localeBundles.length + 1];
        basenames[0] = "lib/i18n/messages";
        System.arraycopy(localeBundles, 0, basenames, 1, localeBundles.length);
        return basenames;
    }
}
