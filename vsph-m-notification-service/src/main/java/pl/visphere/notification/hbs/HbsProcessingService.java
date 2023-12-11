/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.hbs;

import com.github.mustachejava.MustacheFactory;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.i18n.AppLocale;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.notification.config.ExternalServiceConfig;
import pl.visphere.notification.hbs.dto.FontTransporterDto;
import pl.visphere.notification.mail.MailProperties;
import pl.visphere.notification.mail.mjml.MjmlApiService;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
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
    private final ExternalServiceConfig externalServiceConfig;
    private final MailProperties mailProperties;
    private final JwtService jwtService;

    public String parseToRawHtml(
        HbsTemplate template, String title, Map<String, Object> variables, Locale locale, String messageUuid
    ) {
        final HbsLayout layout = template.getLayout();
        String outputHtml;
        try {
            final Map<String, Object> commonVariables = getStringObjectMap(locale);

            final Map<String, Object> templateVariables = new HashMap<>(commonVariables);
            templateVariables.putAll(variables);

            final String templateFragment = compileHbsTemplate(template, templateVariables);

            final FontTransporterDto fontTransporterDto = FontTransporterDto.builder()
                .name(hbsProperties.getFontName())
                .path(hbsProperties.getFontResourcePath())
                .build();

            final String mirrorJwt = jwtService
                .generateNonExpiredToken(messageUuid, Jwts.claims())
                .compact();

            final String mirrorUri = new StringJoiner("/")
                .add(externalServiceConfig.getLandingUrl())
                .add("mirror-email")
                .add(mirrorJwt)
                .toString();

            final Map<String, Object> layoutVariables = new HashMap<>(commonVariables);
            layoutVariables.put("title", title);
            layoutVariables.put("font", fontTransporterDto);
            layoutVariables.put("embedRenderingContent", templateFragment);
            layoutVariables.put("year", LocalDate.now().getYear());
            layoutVariables.put("mobileLinks", hbsProperties.getMobile());
            layoutVariables.put("socialLinks", hbsProperties.getSocial());
            layoutVariables.put("mirrorLink", mirrorUri);

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
            throw new GenericRestException("Unable to process HBS template. Cause: '{}'.", ex.getMessage());
        }
        log.info("Successfully processed email template: '{}' with title: '{}'.", template.getTemplateName(), title);
        return outputHtml;
    }

    private Map<String, Object> getStringObjectMap(Locale locale) {
        String landingUrl = externalServiceConfig.getLandingUrl();
        if (LocaleContextHolder.getLocale() != AppLocale.PL) {
            landingUrl += "/" + locale.getLanguage();
        }
        final Map<String, Object> commonVariables = new HashMap<>();
        commonVariables.put("currentLang", i18nService.getCurrentLocaleCode());
        commonVariables.put("landingUrl", landingUrl);
        commonVariables.put("clientUrl", externalServiceConfig.getClientUrl());
        commonVariables.put("infraGatewayUrl", externalServiceConfig.getInfraGatewayUrl());
        commonVariables.put("s3StaticUrl", externalServiceConfig.getS3StaticUrl());
        commonVariables.put("s3Url", externalServiceConfig.getS3Url());
        commonVariables.put("replyMail", mailProperties.getReplyTo());
        return commonVariables;
    }

    public String parseToRawHtml(HbsTemplate template, String title, Map<String, Object> variables, String messageUuid) {
        return parseToRawHtml(template, title, variables, LocaleContextHolder.getLocale(), messageUuid);
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
