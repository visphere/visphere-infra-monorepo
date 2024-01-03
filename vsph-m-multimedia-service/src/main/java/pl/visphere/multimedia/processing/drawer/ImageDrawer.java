/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing.drawer;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import pl.visphere.lib.exception.app.FileException;
import pl.visphere.multimedia.processing.ImageProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@Slf4j
public class ImageDrawer extends AbstractImageDrawer<byte[]> {
    public ImageDrawer(ImageProperties imageProperties) {
        super(imageProperties);
    }

    @Override
    public byte[] drawImage(byte[] inputData, String color) {
        byte[] resizedImage;
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData)) {
            final BufferedImage inputImage = ImageIO.read(inputStream);
            final BufferedImage resizedData = Scalr.resize(inputImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT,
                size, size, Scalr.OP_ANTIALIAS);
            resizedImage = generateByteArrayFromBufferedImage(resizedData);
        } catch (Exception ex) {
            throw new FileException.FileIsCorruptedException(ex.getMessage());
        }
        return resizedImage;
    }
}
