/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.s3.*;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileEntity;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileRepository;
import pl.visphere.multimedia.exception.AccountProfileException;
import pl.visphere.multimedia.processing.drawer.InitialsDrawer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final InitialsDrawer initialsDrawer;
    private final S3Client s3Client;
    private final S3Helper s3Helper;

    private final AccountProfileRepository accountProfileRepository;

    @Override
    public void generateDefaultProfile(DefaultUserProfileReqDto reqDto) {
        final String randomColor = initialsDrawer.getRandomColor();
        final byte[] imageData = initialsDrawer.drawImage(reqDto.username(), reqDto.initials(), randomColor);

        final FilePayload filePayload = FilePayload.builder()
            .name("profile")
            .data(imageData)
            .extension(FileExtension.JPEG)
            .build();

        final InsertedObjectRes res = s3Client
            .putObject(S3Bucket.USERS, String.valueOf(reqDto.userId()), filePayload);

        final AccountProfileEntity accountProfile = AccountProfileEntity.builder()
            .profileColor(randomColor)
            .profileImageUuid(res.uuid())
            .userId(reqDto.userId())
            .build();

        final AccountProfileEntity savedAccountProfile = accountProfileRepository.save(accountProfile);
        log.info("Successfully generated default profile image: '{}'", savedAccountProfile);
    }

    @Override
    public ProfileImageDetailsResDto getProfileImageDetails(Long userId) {
        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(userId)
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(userId));

        final ProfileImageDetailsResDto resDto = ProfileImageDetailsResDto.builder()
            .profileColor(accountProfile.getProfileColor())
            .profileImageUuid(accountProfile.getProfileImageUuid())
            .profileImagePath(s3Helper.prepareUserProfilePath(userId, accountProfile.getProfileImageUuid()))
            .build();

        log.info("Successfully get account profile details: '{}'", resDto);
        return resDto;
    }
}
