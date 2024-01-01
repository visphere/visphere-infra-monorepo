/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.account;

import jakarta.servlet.http.HttpServletRequest;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.user.network.account.dto.*;

interface AccountService {
    AccountDetailsResDto getAccountDetails(AuthUserDetails user);
    UpdateAccountDetailsResDto updateAccountDetails(UpdateAccountDetailsReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto createNew(CreateAccountReqDto reqDto);
    BaseMessageResDto activate(String token);
    BaseMessageResDto resend(ActivateAccountReqDto reqDto);
    BaseMessageResDto disable(boolean deleteMessages, PasswordReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto enable(HttpServletRequest req);
    BaseMessageResDto delete(boolean deleteMessages, PasswordReqDto reqDto, AuthUserDetails user);
}
