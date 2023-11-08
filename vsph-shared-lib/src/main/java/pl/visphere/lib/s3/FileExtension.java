/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileExtension {
    PNG("png"),
    JPEG("jpeg"),
    GIF("gif"),
    WEBP("webp"),
    HTML_GZ("html.gz"),
    ;

    private final String ext;
}
