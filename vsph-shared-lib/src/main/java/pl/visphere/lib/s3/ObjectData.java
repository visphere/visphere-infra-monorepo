/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.s3;

import lombok.Builder;

@Builder
public record ObjectData(
    String uuid,
    String fullPath
) {
}
