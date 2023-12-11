/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing.drawer;

import lombok.extern.slf4j.Slf4j;
import pl.visphere.multimedia.processing.ImageProperties;
import pl.visphere.multimedia.processing.ResourcesRestLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class LockerDrawer extends AbstractImageDrawer<String> {
    private final BufferedImage lockerAlphaBlend;

    public LockerDrawer(ImageProperties imageProperties, ResourcesRestLoader resourcesRestLoader) {
        super(imageProperties);
        lockerAlphaBlend = resourcesRestLoader.loadLockerAlphaBlend();
    }

    @Override
    public byte[] drawImage(String hexColor, String color) {
        final BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        graphics2D.setPaint(Color.decode(hexColor));
        graphics2D.fillRect(0, 0, size, size);

        graphics2D.drawImage(lockerAlphaBlend, 0, 0, null);
        graphics2D.dispose();

        log.info("Successfully generated locker alpha blend image with color: '{}'.", hexColor);
        return generateByteArrayFromBufferedImage(bufferedImage);
    }
}
