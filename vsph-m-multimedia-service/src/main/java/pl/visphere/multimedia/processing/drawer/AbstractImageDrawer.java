/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing.drawer;

import lombok.extern.slf4j.Slf4j;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.s3.FileExtension;
import pl.visphere.multimedia.processing.ImageProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Slf4j
public abstract class AbstractImageDrawer<I> {
    protected static final FileExtension DEF_EXT = FileExtension.PNG;
    private static final Random RANDOM = new Random();

    protected final ImageProperties imageProperties;
    protected final int size;
    protected final int fontSize;

    protected AbstractImageDrawer(ImageProperties imageProperties) {
        this.imageProperties = imageProperties;
        this.size = imageProperties.getSize();
        this.fontSize = imageProperties.getFontSize();
    }

    public String getRandomColor() {
        final List<String> availableColors = imageProperties.getColors();
        return availableColors.get(RANDOM.nextInt(availableColors.size()));
    }

    protected byte[] generateByteArrayFromBufferedImage(BufferedImage bufferedImage) {
        final byte[] outputData;
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, DEF_EXT.getExt(), outputStream);
            outputData = outputStream.toByteArray();
        } catch (IOException ex) {
            throw new GenericRestException("Unable to convert BufferedImage to bytes array. Cause: '{}'.",
                ex.getMessage());
        }
        return outputData;
    }

    public FileExtension getFileExtension() {
        return DEF_EXT;
    }

    public abstract byte[] drawImage(I inputData, String color);
}
