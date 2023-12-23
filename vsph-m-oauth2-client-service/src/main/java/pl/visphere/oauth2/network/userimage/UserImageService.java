/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.userimage;

import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.oauth2.network.userimage.dto.ImageProviderResDto;

interface UserImageService {
    ImageProviderResDto toggleProviderProfileImage(boolean fromProvider, AuthUserDetails user);
}
