/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.multimedia.domain.ImageType;
import pl.visphere.multimedia.i18n.LocaleSet;

public class ColorException {
    @Slf4j
    public static class ColorUpdateNotAvailableException extends AbstractRestException {
        public ColorUpdateNotAvailableException(ImageType imageType) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.COLOR_UPDATE_NOT_AVAILABLE_EXCEPTION_MESSAGE);
            log.error("Attempt to update color for image type: '{}' which is not available.", imageType);
        }
    }
}
