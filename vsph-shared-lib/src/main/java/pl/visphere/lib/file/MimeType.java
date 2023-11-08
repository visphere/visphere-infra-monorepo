/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MimeType {
    PNG("image/png", "png"),
    JPEG("image/jpeg", "jpeg"),
    JPG("image/jpg", "jpg"),
    GZIP("application/gzip", "gzip"),
    ;

    private final String mime;
    private final String name;
}
