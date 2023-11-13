/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.LocaleResolver;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.MessageExceptionResDto;
import pl.visphere.lib.i18n.I18nService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@RequiredArgsConstructor
abstract class AbstractAuthResolver {
    private final I18nService i18nService;
    private final LocaleResolver localeResolver;
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected void chainRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        final Locale locale = localeResolver.resolveLocale(req);
        LocaleContextHolder.setLocale(locale);

        final HttpStatus status = HttpStatus.FORBIDDEN;
        final MessageExceptionResDto resDto = new MessageExceptionResDto(status, req,
            i18nService.getMessage(LibLocaleSet.SECURITY_AUTHENTICATION_EXCEPTION_MESSAGE));
        final String payload = objectMapper.writeValueAsString(resDto);

        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        res.getWriter().println(payload);
    }
}
