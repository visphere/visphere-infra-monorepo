/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing.drawer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.multimedia.processing.ImageProperties;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.StringJoiner;

@Slf4j
public class InitialsDrawer extends AbstractImageDrawer<char[]> {
    public InitialsDrawer(ImageProperties imageProperties) {
        super(imageProperties);
        loadCustomFontFromFile();
    }

    private void loadCustomFontFromFile() {
        try {
            final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final ClassPathResource resource = new ClassPathResource(imageProperties.getFontPath());
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, resource.getFile()));
            log.info("Successfully registered custom font: '{}'", imageProperties.getFontPath());
        } catch (IOException | FontFormatException ex) {
            log.error("Unable to load custom font from file. Cause: '{}'", ex.getMessage());
            throw new GenericRestException();
        }
    }

    @Override
    public byte[] drawImage(char[] inputData, String color) {
        final StringJoiner joiner = new StringJoiner(StringUtils.EMPTY);
        for (final char letter : inputData) {
            joiner.add(String.valueOf(letter).toUpperCase());
        }
        final String initials = joiner.toString();
        final BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        graphics2D.setFont(new Font(imageProperties.getFontName(), Font.PLAIN, fontSize));
        graphics2D.setPaint(Color.decode(color));
        graphics2D.fillRect(0, 0, size, size);
        graphics2D.setColor(Color.WHITE);

        final FontMetrics fontMetrics = graphics2D.getFontMetrics();
        final int xPos = (size - fontMetrics.stringWidth(initials)) / 2;
        final int yPos = ((size - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();

        graphics2D.drawString(initials, xPos, yPos);
        graphics2D.dispose();

        log.info("Successfully generated initials image with initials: '{}' and color: '{}'", initials, color);
        return generateByteArrayFromBufferedImage(bufferedImage);
    }
}
