/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.service.image;

import pl.visphere.lib.kafka.payload.multimedia.*;

public interface ImageService {
    ProfileImageDetailsResDto generateDefaultProfile(DefaultUserProfileReqDto reqDto);
    ProfileImageDetailsResDto updateDefaultProfile(UpdateUserProfileReqDto reqDto);
    void replaceProfileWithLocked(Long userId);
    void replaceLockedWithProfile(Long userId);
    DefaultGuildProfileResDto generateDefaultGuildProfile(DefaultGuildProfileReqDto reqDto);
    DefaultGuildProfileResDto updateDefaultGuildProfile(DefaultGuildProfileReqDto reqDto);
    ProfileImageDetailsResDto getProfileImageDetails(ProfileImageDetailsReqDto reqDto);
    ProfileImageDetailsResDto getGuildProfileImageDetails(Long guildId);
    GuildImageByIdsResDto getGuildImagesByGuildIds(GuildImageByIdsReqDto reqDto);
    void deleteUserImageData(Long userId);
    void deleteGuildImageData(Long guildId);
}
