/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.multimedia;

import java.util.Map;

public record UsersImagesDetailsResDto(
    Map<Long, String> imagesDetails
) {
}
