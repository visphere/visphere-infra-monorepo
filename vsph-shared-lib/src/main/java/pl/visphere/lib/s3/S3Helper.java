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
        return prepareProfilePath(S3Bucket.USERS, id, uuid);
    }

    public String prepareGuildProfilePath(Long id, String uuid) {
        return prepareProfilePath(S3Bucket.SPHERES, id, uuid);
    }

    public String prepareProfilePath(S3Bucket bucket, Long id, String uuid) {
        return new StringJoiner("/")
            .add(CdnProperty.CDN_BASE_URL.getValue(environment))
            .add(bucket.getName())
            .add(String.valueOf(id))
            .add(S3ResourcePrefix.PROFILE.getPrefix() + "-" + uuid + "." + FileExtension.PNG.getExt())
            .toString();
    }
}
