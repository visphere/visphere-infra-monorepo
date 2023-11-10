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
    MFA_IS_ALREADY_SETUP_EXCEPTION_MESSAGE("vsph.auth.exc.mfaIsAlreadySetup"),
    MFA_NOT_ENABLED_EXCEPTION_MESSAGE("vsph.auth.exc.mfaNotEnabled"),
    MFA_INVALID_CODE_EXCEPTION_MESSAGE("vsph.auth.exc.mfaInvalidCode"),
    CREATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.createAccount.res.success"),
    ACTIVATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.activateAccount.res.success"),
    RESEND_ACTIVATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.resendActivateAccount.res.success"),
    SEND_ALT_MFA_EMAIL_CODE_RESPONSE_SUCCESS("vsph.i18n.altMfaSendEmailCode.res.success"),
    ;

    private final String holder;
}
