/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.userprofile;

import org.springframework.web.multipart.MultipartFile;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.multimedia.network.userprofile.dto.MessageWithResourcePathResDto;

interface UserProfileService {
    MessageWithResourcePathResDto uploadProfileImage(MultipartFile image, AuthUserDetails user);
    MessageWithResourcePathResDto generateGravatarImage(AuthUserDetails user);
    BaseMessageResDto deleteProfileImage(AuthUserDetails user);
}
