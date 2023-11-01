/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.profileimage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.multimedia.dto.MessageWithResourcePathResDto;

@RestController
@RequestMapping("/api/v1/multimedia/profile/image")
@RequiredArgsConstructor
class ProfileImageController {
    private final ProfileImageService userProfileService;

    @PostMapping("/custom")
    ResponseEntity<MessageWithResourcePathResDto> uploadProfileImage(
        @RequestParam("image") MultipartFile image,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(userProfileService.uploadProfileImage(image, user));
    }

    @PostMapping("/identicon")
    ResponseEntity<MessageWithResourcePathResDto> generateIdenticon(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(userProfileService.generateIdenticonImage(user));
    }

    @DeleteMapping
    ResponseEntity<MessageWithResourcePathResDto> deleteProfileImage(@LoggedUser AuthUserDetails user) {
        return ResponseEntity.ok(userProfileService.deleteProfileImage(user));
    }
}
