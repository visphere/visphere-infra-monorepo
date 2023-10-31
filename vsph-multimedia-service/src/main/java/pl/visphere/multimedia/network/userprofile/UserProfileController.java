/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.userprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.multimedia.network.userprofile.dto.MessageWithResourcePathResDto;

@RestController
@RequestMapping("/api/v1/multimedia/user/profile")
@RequiredArgsConstructor
class UserProfileController {
    private final UserProfileService userProfileService;

    @PostMapping("/image/custom")
    ResponseEntity<MessageWithResourcePathResDto> uploadProfileImage(
        @RequestParam("image") MultipartFile image,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(userProfileService.uploadProfileImage(image, user));
    }

    @PostMapping("/image/gravatar")
    ResponseEntity<MessageWithResourcePathResDto> generateGravatar(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(userProfileService.generateGravatarImage(user));
    }

    @DeleteMapping("/image")
    ResponseEntity<BaseMessageResDto> deleteProfileImage(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(userProfileService.deleteProfileImage(user));
    }
}
