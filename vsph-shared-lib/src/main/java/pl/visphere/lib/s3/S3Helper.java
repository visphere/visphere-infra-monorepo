/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

import java.util.StringJoiner;

@RequiredArgsConstructor
public class S3Helper {
    private final Environment environment;

    public String prepareUserProfilePath(Long id, String uuid) {
        return new StringJoiner("/")
            .add(S3Property.CDN_BASE_URL.getValue(environment))
            .add(S3Bucket.USERS.getName())
            .add(String.valueOf(id))
            .add("profile-" + uuid + "." + FileExtension.JPEG.getExt())
            .toString();
    }
}
