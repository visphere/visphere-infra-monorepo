/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.renewpassword;

import jakarta.servlet.http.HttpServletRequest;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.user.network.renewpassword.dto.AttemptReqDto;
import pl.visphere.user.network.renewpassword.dto.ChangeReqDto;
import pl.visphere.user.network.renewpassword.dto.ChangeViaAccountReqDto;

interface PasswordRenewService {
    BaseMessageResDto request(AttemptReqDto reqDto);
    BaseMessageResDto verify(String token);
    BaseMessageResDto resend(AttemptReqDto reqDto);
    BaseMessageResDto change(String token, ChangeReqDto reqDto);
    BaseMessageResDto changeViaAccount(HttpServletRequest req, ChangeViaAccountReqDto reqDto, AuthUserDetails user);
}
