/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.s3;

import lombok.Builder;

@Builder
public record FilePayload(
    S3ResourcePrefix prefix,
    byte[] data,
    FileExtension extension,
    String uuid
) {
    public FilePayload() {
        this(S3ResourcePrefix.PROFILE, null, FileExtension.PNG, null);
    }

    public FilePayload(byte[] imageData) {
        this(S3ResourcePrefix.PROFILE, imageData, FileExtension.PNG, null);
    }

    public FilePayload(byte[] imageData, FileExtension extension) {
        this(S3ResourcePrefix.PROFILE, imageData, extension, null);
    }
}
