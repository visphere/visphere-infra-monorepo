/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3Bucket {
    SPHERES("spheres"),
    USERS("users"),
    EMAILS("emails"),
    LOCKED_USERS("locked-users"),
    ATTACHMENTS("attachments"),
    ;

    private final String name;
}
