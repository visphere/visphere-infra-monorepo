/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
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
