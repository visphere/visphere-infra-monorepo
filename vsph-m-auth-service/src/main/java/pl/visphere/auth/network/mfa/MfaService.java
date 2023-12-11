/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.mfa;

import jakarta.servlet.http.HttpServletRequest;
import pl.visphere.auth.network.LoginResDto;
import pl.visphere.auth.network.mfa.dto.MfaAuthenticatorDataResDto;
import pl.visphere.auth.network.mfa.dto.MfaCredentialsReqDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;

interface MfaService {
    MfaAuthenticatorDataResDto authenticatorData(MfaCredentialsReqDto reqDto);
    LoginResDto authenticatorSetOrVerify(String code, MfaCredentialsReqDto reqDto, boolean isFirstTime);
    BaseMessageResDto altSendEmail(MfaCredentialsReqDto reqDto);
    LoginResDto altVerifyEmailToken(String token, MfaCredentialsReqDto reqDto);
    BaseMessageResDto toggleMfaAccountState(boolean isEnabled, AuthUserDetails user);
    BaseMessageResDto resetMfaSetup(HttpServletRequest req, boolean logoutFromAll, AuthUserDetails user);
}
