/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.userimage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.lib.security.user.LoggedUser;
import pl.visphere.oauth2.network.userimage.dto.ImageProviderResDto;

@RestController
@RequestMapping("/api/v1/oauth2/image/user")
@RequiredArgsConstructor
class UserImageController {
    private final UserImageService userImageService;

    @PatchMapping("/provider")
    ResponseEntity<ImageProviderResDto> toggleProviderProfileImage(
        @RequestParam boolean fromProvider,
        @LoggedUser AuthUserDetails user
    ) {
        return ResponseEntity.ok(userImageService.toggleProviderProfileImage(fromProvider, user));
    }
}
