/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.hbs;

import com.github.mustachejava.MustacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.notification.hbs.dto.FontTransporterDto;
import pl.visphere.notification.mjml.MjmlApiService;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class HbsProcessingService {
    private final HbsProperties hbsProperties;
    private final I18nService i18nService;
    private final ResourceLoader resourceLoader;
    private final MjmlApiService mjmlApiService;
    private final MustacheFactory mustacheFactory;

    public String parseToRawHtml(HbsTemplate template, String title, Map<String, Object> variables) {
        final HbsLayout layout = template.getLayout();
        String outputHtml;
        try {
            final String templateFragment = compileHbsTemplate(template, variables);

            final FontTransporterDto fontTransporterDto = FontTransporterDto.builder()
                .name(hbsProperties.getFontName())
                .path(hbsProperties.getFontResourcePath())
                .build();

            final Map<String, Object> layoutVariables = new HashMap<>();
            layoutVariables.put("currentLang", LocaleContextHolder.getLocale().getLanguage());
            layoutVariables.put("title", title);
            layoutVariables.put("font", fontTransporterDto);
            layoutVariables.put("embedRenderingContent", templateFragment);
            layoutVariables.put("year", LocalDate.now().getYear());

            final String compositeClearedTemplate = compileHbsTemplate(layout, layoutVariables)
                .replaceAll("(?m)^[ \t]*\r?\n", "");

            final Pattern pattern = Pattern.compile("\\[\\[(.*?)]]");
            final Matcher matcher = pattern.matcher(compositeClearedTemplate);

            final StringBuilder builder = new StringBuilder();
            while (matcher.find()) {
                matcher.appendReplacement(builder, i18nService.getMessage(matcher.group(1)));
            }
            matcher.appendTail(builder);

            outputHtml = mjmlApiService.sendRequestForParse(builder.toString());
        } catch (IOException ex) {
            log.error("Unable to process HBS template. Cause: '{}'", ex.getMessage());
            throw new GenericRestException();
        }
        log.info("Successfully processed email template: '{}' with title: '{}'", template.getTemplateName(), title);
        return outputHtml;
    }

    private String compileHbsTemplate(HbsFile file, Map<String, Object> variables) throws IOException {
        final String path = file.getPath(hbsProperties);
        final String stringContent = resourceLoader
            .getResource(path)
            .getContentAsString(StandardCharsets.UTF_8);

        final StringReader reader = new StringReader(stringContent);
        final StringWriter writer = new StringWriter();

        mustacheFactory
            .compile(reader, path)
            .execute(writer, variables);

        return writer.toString();
    }
}
