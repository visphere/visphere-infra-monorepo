/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.profilecolor;

import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.multimedia.dto.MessageWithResourcePathResDto;
import pl.visphere.multimedia.network.profilecolor.dto.UpdateProfileColorReqDto;

import java.util.List;

public interface ProfileColorService {
    List<String> getColors();
    MessageWithResourcePathResDto updateProfileColor(UpdateProfileColorReqDto reqDto, AuthUserDetails user);
}
