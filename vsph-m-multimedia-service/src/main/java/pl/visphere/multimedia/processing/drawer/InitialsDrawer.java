/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing.drawer;

import pl.visphere.multimedia.processing.ImageProperties;

public class InitialsDrawer extends AbstractImageDrawer<char[]> {
    public InitialsDrawer(ImageProperties imageProperties) {
        super(imageProperties);
    }

    @Override
    public byte[] drawImage(String inputSeed, char[] inputData, String color) {
        return new byte[0];
    }
}
