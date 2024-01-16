/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.visphere.multimedia.config.ExternalServiceConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourcesRestLoader {
    private final RestTemplate restTemplate;
    private final ImageProperties imageProperties;
    private final ExternalServiceConfig externalServiceConfig;

    public Font loadFontFromExternalServer() {
        Font font;
        final String path = imageProperties.getFontPath();
        final byte[] fontData = loadResourceFromRemoteServer(path);
        try (final InputStream inputStream = new ByteArrayInputStream(fontData)) {
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            log.info("Successfully loaded font graphics resource from external server: '{}'.", path);
        } catch (IOException | FontFormatException ex) {
            throw new RuntimeException("Unable to process image font resource. Cause: '" + ex.getMessage() + "'.");
        }
        return font;
    }

    public BufferedImage loadLockerAlphaBlend() {
        BufferedImage lockedAlphaBlend;
        final String path = imageProperties.getLockerAlphaBlendPath();
        final byte[] lockedImageData = loadResourceFromRemoteServer(path);
        try (final InputStream inputStream = new ByteArrayInputStream(lockedImageData)) {
            lockedAlphaBlend = ImageIO.read(inputStream);
            log.info("Successfully loaded locker alpha blend image from external server: '{}'", path);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to process locked alpha blend resource. Cause: '" + ex.getMessage() + "'.");
        }
        return lockedAlphaBlend;
    }

    private byte[] loadResourceFromRemoteServer(String resourceUri) {
        byte[] rawData;
        try {
            final StringJoiner joiner = new StringJoiner("/")
                .add(externalServiceConfig.getS3StaticUrl())
                .add("static")
                .add(resourceUri);
            rawData = restTemplate
                .getForEntity(joiner.toString(), byte[].class)
                .getBody();
        } catch (RestClientException ex) {
            throw new RuntimeException("Unable to load resource from server: " + resourceUri + ". Cause: '"
                + ex.getMessage() + "'.");
        }
        return rawData;
    }
}
