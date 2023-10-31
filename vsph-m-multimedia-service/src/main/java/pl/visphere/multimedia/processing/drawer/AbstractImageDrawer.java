/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing.drawer;

import pl.visphere.lib.s3.FileExtension;
import pl.visphere.multimedia.processing.ImageProperties;

import java.util.List;
import java.util.Random;

public abstract class AbstractImageDrawer<I> {
    protected static int WIDTH_PX = 100;
    protected static int HEIGHT_PX = 100;
    protected static FileExtension DEF_EXT = FileExtension.JPEG;

    protected final ImageProperties imageProperties;
    private final Random RANDOM = new Random();

    protected AbstractImageDrawer(ImageProperties imageProperties) {
        this.imageProperties = imageProperties;
    }

    public String getRandomColor() {
        final List<String> availableColors = imageProperties.getColors();
        return availableColors.get(RANDOM.nextInt(availableColors.size()));
    }

    public abstract byte[] drawImage(String inputSeed, I inputData, String color);
}
