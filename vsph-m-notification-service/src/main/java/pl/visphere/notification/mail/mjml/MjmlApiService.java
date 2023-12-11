/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.mail.mjml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.visphere.lib.exception.GenericRestException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MjmlApiService {
    private final RestTemplate restTemplate;
    private final MjmlApiProperties mjmlApiProperties;

    public String sendRequestForParse(String rawHtml) {
        final MjmlParserReqDto reqDto = MjmlParserReqDto.builder()
            .rawData(rawHtml)
            .build();

        final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(mjmlApiProperties.getHeaderKeySignature(), mjmlApiProperties.getApiKey());

        final String url = mjmlApiProperties.getHost() + "/api/v1/email/parse";
        String parsedHtml;
        try {
            final HttpEntity<MjmlParserReqDto> httpEntity = new HttpEntity<>(reqDto, headers);
            final ResponseEntity<String> parsedTemplate = restTemplate
                .exchange(url, HttpMethod.POST, httpEntity, String.class);

            parsedHtml = parsedTemplate.getBody();
        } catch (RestClientException ex) {
            throw new GenericRestException("Unable to call email template parser service. Cause: '{}'.",
                ex.getMessage());
        }
        log.info("Successfully processed email template by external MJML service.");
        return parsedHtml;
    }
}
