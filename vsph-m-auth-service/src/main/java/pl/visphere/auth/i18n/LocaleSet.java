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
    MFA_CURRENTLY_ENABLED_EXCEPTION_MESSAGE("vsph.auth.exc.mfaCurrentlyEnabled"),
    MFA_CURRENTLY_DISABLED_EXCEPTION_MESSAGE("vsph.auth.exc.mfaCurrentlyDisabled"),
    MFA_INVALID_CODE_EXCEPTION_MESSAGE("vsph.auth.exc.mfaInvalidCode"),
    INVALID_OLD_PASSWORD_EXCEPTION_MESSAGE("vsph.auth.exc.invalidOldPassword"),
    ACCOUNT_ALREADY_ENABLED_EXCEPTION_MESSAGE("vsph.auth.exc.accountAlreadyEnabled"),
    ACCOUNT_ALREADY_DISABLED_EXCEPTION_MESSAGE("vsph.auth.exc.accountAlreadyDisabled"),
    UNABLE_TO_DELETE_ACCOUNT_WITH_GUILDS_EXCEPTION_MESSAGE("vsph.auth.exc.unableToDeleteAccountWithGuilds"),
    CREATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.createAccount.res.success"),
    ACTIVATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.activateAccount.res.success"),
    RESEND_ACTIVATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.resendActivateAccount.res.success"),
    SEND_ALT_MFA_EMAIL_CODE_RESPONSE_SUCCESS("vsph.i18n.altMfaSendEmailCode.res.success"),
    REQUEST_FIRST_EMAIL_ADDRESS_RESPONSE_SUCCESS("vsph.i18n.requestFirstEmailAddress.res.success"),
    RESEND_REQUEST_FIRST_EMAIL_ADDRESS_RESPONSE_SUCCESS("vsph.i18n.resendRequestFirstEmailAddress.res.success"),
    UPDATE_FIRST_EMAIL_ADDRESS_RESPONSE_SUCCESS("vsph.i18n.updateFirstEmailAddress.res.success"),
    REQUEST_SECOND_EMAIL_ADDRESS_RESPONSE_SUCCESS("vsph.i18n.requestSecondEmailAddress.res.success"),
    RESEND_REQUEST_SECOND_EMAIL_ADDRESS_RESPONSE_SUCCESS("vsph.i18n.resendRequestSecondEmailAddress.res.success"),
    UPDATE_SECOND_EMAIL_ADDRESS_RESPONSE_SUCCESS("vsph.i18n.updateSecondEmailAddress.res.success"),
    REMOVE_SECOND_EMAIL_ADDRESS_RESPONSE_SUCCESS("vsph.i18n.removeSecondEmailAddress.res.success"),
    MFA_UPDATE_SETTINGS_RESPONSE_SUCCESS("vsph.i18n.mfaUpdateSettings.res.success"),
    MFA_RESET_SETTINGS_RESPONSE_SUCCESS("vsph.i18n.mfaResetSettings.res.success"),
    UDPATE_ACCOUNT_DETAILS_RESPONSE_SUCCESS("vsph.i18n.updatedAccountDetails.res.success"),
    DISABLED_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.disabledAccount.res.success"),
    ENABLED_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.enabledAccount.res.success"),
    DELETED_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.deletedAccount.res.success"),
    ;

    private final String holder;
}
