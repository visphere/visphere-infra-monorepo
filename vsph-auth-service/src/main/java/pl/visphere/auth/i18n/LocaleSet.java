/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.ILocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements ILocaleExtendableSet {
    LOGIN_RESPONSE_ERROR("vsph.i18n.login.res.error"),
    ATTEMPT_CHANGE_PASSWORD_RESPONSE_ERROR("vsph.i18n.attemptChangePassword.res.error"),
    ATTEMPT_CHANGE_PASSWORD_RESPONSE_SUCCESS("vsph.i18n.attemptChangePassword.res.success"),
    TOKEN_VERIFICATION_RESPONSE_ERROR("vsph.i18n.tokenVerification.res.error"),
    TOKEN_VERIFICATION_RESPONSE_SUCCESS("vsph.i18n.tokenVerification.res.success"),
    RESEND_TOKEN_VERIFICATION_RESPONSE_ERROR("vsph.i18n.resendTokenVerification.res.error"),
    RESEND_TOKEN_VERIFICATION_RESPONSE_SUCCESS("vsph.i18n.resendTokenVerification.res.success"),
    CHANGE_PASSWORD_RESPONSE_ERROR("vsph.i18n.changePassword.res.error"),
    CHANGE_PASSWORD_RESPONSE_SUCCESS("vsph.i18n.changePassword.res.success"),
    LOGOUT_RESPONSE_ERROR("vsph.i18n.logout.res.error"),
    LOGOUT_RESPONSE_SUCCESS("vsph.i18n.logout.res.success");

    private final String holder;
}