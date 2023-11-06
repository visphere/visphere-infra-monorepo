/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing.drawer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.multimedia.processing.ImageProperties;

import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class IdenticonDrawer extends AbstractImageDrawer<String> {
    private static final int SECTOR_SIZE = 34;
    private static final int MARGIN = 40;

    public IdenticonDrawer(ImageProperties imageProperties) {
        super(imageProperties);
    }

    @Override
    public byte[] drawImage(String inputData, String color) {
        byte[] identiconAsBytes;
        try {
            final String md5 = DigestUtils.md5Hex(inputData);
            final byte[] md5AsBytesArray = Hex.decodeHex(md5);

            final int length = (size - MARGIN * 2) / SECTOR_SIZE;
            final byte[][] grid = new byte[length][length];

            int idx = 0;
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length / 2 + 1; j++) {
                    grid[i][j] = md5AsBytesArray[idx++];
                }
                for (int j = length / 2 + 1; j < length; j++) {
                    grid[i][j] = md5AsBytesArray[idx - (j - (length / 2 + 1) + length / 2)];
                }
            }

            final BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            final Graphics2D graphics2D = bufferedImage.createGraphics();
            final Color background = Color.decode(color);

            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            graphics2D.setColor(background);
            graphics2D.fillRect(0, 0, size, size);

            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    graphics2D.setColor(grid[i][j] % 2 == 0 ? Color.WHITE : background);
                    graphics2D.fillRect(j * SECTOR_SIZE + MARGIN, i * SECTOR_SIZE + MARGIN, SECTOR_SIZE, SECTOR_SIZE);
                }
            }

            graphics2D.dispose();
            identiconAsBytes = generateByteArrayFromBufferedImage(bufferedImage);

            log.info("Successfuly generated indeticon image with username: '{}' and color: '{}'", inputData, color);
        } catch (Exception ex) {
            log.error("Unable to create identicon image. Cause: '{}'", ex.getMessage());
            throw new GenericRestException();
        }
        return identiconAsBytes;
    }
}
