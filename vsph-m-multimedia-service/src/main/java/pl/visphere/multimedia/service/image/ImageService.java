/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.service.image;

import pl.visphere.lib.kafka.payload.multimedia.DefaultGuildProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.DefaultGuildProfileResDto;
import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;

public interface ImageService {
    ProfileImageDetailsResDto generateDefaultProfile(DefaultUserProfileReqDto reqDto);
    void replaceProfileWithLocked(Long userId);
    void replaceLockedWithProfile(Long userId);
    DefaultGuildProfileResDto generateDefaultGuildProfile(DefaultGuildProfileReqDto reqDto);
    DefaultGuildProfileResDto updateDefaultGuildProfile(DefaultGuildProfileReqDto reqDto);
    ProfileImageDetailsResDto getProfileImageDetails(Long userId);
}
