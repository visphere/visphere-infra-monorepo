/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib.i18n;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

class CustomHeaderLocaleResolver extends AcceptHeaderLocaleResolver {

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
