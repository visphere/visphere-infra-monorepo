/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing.drawer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pl.visphere.multimedia.processing.ImageProperties;
import pl.visphere.multimedia.processing.ResourcesRestLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.StringJoiner;

@Slf4j
public class InitialsDrawer extends AbstractImageDrawer<char[]> {
    public InitialsDrawer(ImageProperties imageProperties, ResourcesRestLoader resourcesRestLoader) {
        super(imageProperties);
        resourcesRestLoader.loadFontFromExternalServer();
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
        graphics2D.setColor(calcFontColor(color));

        final FontMetrics fontMetrics = graphics2D.getFontMetrics();
        final int xPos = (size - fontMetrics.stringWidth(initials)) / 2;
        final int yPos = ((size - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();

        graphics2D.drawString(initials, xPos, yPos);
        graphics2D.dispose();

        log.info("Successfully generated initials image with initials: '{}' and color: '{}'.", initials, color);
        return generateByteArrayFromBufferedImage(bufferedImage);
    }

    private Color calcFontColor(String colorHex) {
        if (imageProperties.getColors().stream().anyMatch(c -> c.equals(colorHex))) {
            return Color.decode(imageProperties.getDefaultFontLight());
        }
        final Color inputColor = Color.decode(colorHex);
        final int r = inputColor.getRed();
        final int g = inputColor.getGreen();
        final int b = inputColor.getBlue();
        final double yiq = ((r * 299) + (g * 587) + (b * 114)) / 1_000.0;
        return Color.decode(yiq > 140 ? imageProperties.getDefaultFontDark() : imageProperties.getDefaultFontLight());
    }
}
