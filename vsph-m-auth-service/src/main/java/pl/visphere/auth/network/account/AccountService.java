/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.account;

import jakarta.servlet.http.HttpServletRequest;
import pl.visphere.auth.network.account.dto.*;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;

interface AccountService {
    AccountDetailsResDto getAccountDetails(AuthUserDetails user);
    UpdateAccountDetailsResDto updateAccountDetails(UpdateAccountDetailsReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto createNew(CreateAccountReqDto reqDto);
    BaseMessageResDto activate(String token);
    BaseMessageResDto resend(ActivateAccountReqDto reqDto);
    BaseMessageResDto disable(PasswordReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto enable(HttpServletRequest req);


    BaseMessageResDto delete(PasswordReqDto reqDto, AuthUserDetails user);
}
