/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.renewpassword;

import pl.visphere.auth.network.renewpassword.dto.AttemptReqDto;
import pl.visphere.auth.network.renewpassword.dto.ChangeReqDto;
import pl.visphere.auth.network.renewpassword.dto.ChangeViaAccountReqDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;

interface PasswordRenewService {
    BaseMessageResDto request(AttemptReqDto reqDto);
    BaseMessageResDto verify(String token);
    BaseMessageResDto resend(AttemptReqDto reqDto);
    BaseMessageResDto change(String token, ChangeReqDto reqDto);
    BaseMessageResDto changeViaAccount(ChangeViaAccountReqDto reqDto, AuthUserDetails user);
}
