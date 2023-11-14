/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.user;

import pl.visphere.oauth2.network.user.dto.GetFillDataResDto;
import pl.visphere.oauth2.network.user.dto.LoginResDto;
import pl.visphere.oauth2.network.user.dto.UpdateFillDataReqDto;

interface UserService {
    GetFillDataResDto getUserData(String token);
    LoginResDto fillUserData(UpdateFillDataReqDto reqDto, String token);
    LoginResDto loginViaProvider(String token);
}
