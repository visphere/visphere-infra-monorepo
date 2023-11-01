/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.profileimage;

import org.springframework.web.multipart.MultipartFile;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.multimedia.dto.MessageWithResourcePathResDto;

interface ProfileImageService {
    MessageWithResourcePathResDto uploadProfileImage(MultipartFile image, AuthUserDetails user);
    MessageWithResourcePathResDto generateIdenticonImage(AuthUserDetails user);
    MessageWithResourcePathResDto deleteProfileImage(AuthUserDetails user);
}
