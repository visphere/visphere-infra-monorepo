/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.service.image;

import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;

public interface ImageService {
    void generateDefaultProfile(DefaultUserProfileReqDto reqDto);
    ProfileImageDetailsResDto getProfileImageDetails(Long userId);
}
