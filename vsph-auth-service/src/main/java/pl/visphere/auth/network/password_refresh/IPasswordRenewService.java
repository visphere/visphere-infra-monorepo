/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.password_refresh;

import pl.visphere.auth.network.password_refresh.dto.AttemptReqDto;
import pl.visphere.auth.network.password_refresh.dto.ChangeReqDto;
import pl.visphere.lib.BaseMessageResDto;

interface IPasswordRenewService {
    BaseMessageResDto request(AttemptReqDto reqDto);
    BaseMessageResDto verify(String token);
    BaseMessageResDto resend(AttemptReqDto reqDto);
    BaseMessageResDto change(String token, ChangeReqDto reqDto);
}
