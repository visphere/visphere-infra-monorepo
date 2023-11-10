/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements LocaleExtendableSet {
    ATTEMPT_CHANGE_PASSWORD_RESPONSE_SUCCESS("vsph.i18n.attemptChangePassword.res.success"),
    TOKEN_VERIFICATION_RESPONSE_SUCCESS("vsph.i18n.tokenVerification.res.success"),
    RESEND_TOKEN_VERIFICATION_RESPONSE_SUCCESS("vsph.i18n.resendTokenVerification.res.success"),
    CHANGE_PASSWORD_RESPONSE_SUCCESS("vsph.i18n.changePassword.res.success"),
    LOGOUT_RESPONSE_ERROR("vsph.i18n.logout.res.error"),
    LOGOUT_RESPONSE_SUCCESS("vsph.i18n.logout.res.success"),
    REFRESH_TOKEN_NOT_FOUND_EXCEPTION_MESSAGE("vsph.auth.exc.refreshTokenNotFound"),
    REFRESH_TOKEN_EXPIRED_EXCEPTION_MESSAGE("vsph.auth.exc.refreshTokenExpired"),
    CREATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.createAccount.res.success"),
    ACTIVATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.activateAccount.res.success"),
    RESEND_ACTIVATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.resendActivateAccount.res.success"),
    RESEND_ACTIVATE_ACCOUNT_RESPONSE_ERROR("vsph.i18n.resendActivateAccount.res.error");
    
    private final String holder;
}
