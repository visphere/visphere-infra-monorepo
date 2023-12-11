/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements LocaleExtendableSet {
    TOGGLE_USER_MAIL_NOTIF_STATE_RESPONSE_SUCCESS("vsph.i18n.toggleUserMailNotifState.res.success"),
    USER_NOTIFS_NOT_FOUND_EXCEPTION_MESSAGE("vsph.notifications.exc.userNotifsNotFound"),
    MAIL_ACTIVATE_ACCOUNT_TITLE("vsph.i18n.mail.activateAccount.title"),
    MAIL_CHANGE_PASSWORD_TITLE("vsph.i18n.mail.changePassword.title"),
    MAIL_MFA_CODE_TITLE("vsph.i18n.mail.mfaCode.title"),
    MAIL_UPDATED_MFA_STATE_TITLE("vsph.i18n.mail.updatedMfaState.title"),
    MAIL_RESET_MFA_STATE_TITLE("vsph.i18n.mail.resetMfaState.title"),
    MAIL_PASSWORD_CHANGED_TITLE("vsph.i18n.mail.passwordChanged.title"),
    MAIL_NEW_ACCOUNT_TITLE("vsph.i18n.mail.newAccount.title"),
    MAIL_REQ_UPDATE_EMAIL_TITLE("vsph.i18n.mail.reqUpdateEmail.title"),
    MAIL_REQ_UPDATE_SECOND_EMAIL_TITLE("vsph.i18n.mail.reqUpdateSecondEmail.title"),
    MAIL_UPDATED_EMAIL_TITLE("vsph.i18n.mail.updatedEmail.title"),
    MAIL_UPDATED_SECOND_EMAIL_TITLE("vsph.i18n.mail.updatedSecondEmail.title"),
    MAIL_REMOVED_SECOND_EMAIL_TITLE("vsph.i18n.mail.removedSecondEmail.title"),
    MAIL_ENABLED_ACCOUNT_TITLE("vsph.i18n.mail.enabledAccount.title"),
    MAIL_DISABLED_ACCOUNT_TITLE("vsph.i18n.mail.disabledAccount.title"),
    ;

    private final String holder;
}
