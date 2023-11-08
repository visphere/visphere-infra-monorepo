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
    MAIL_ACTIVATE_ACCOUNT_TITLE("vsph.i18n.mail.activateAccount.title"),
    MAIL_CHANGE_PASSWORD_TITLE("vsph.i18n.mail.changePassword.title"),
    MAIL_PASSWORD_CHANGED_TITLE("vsph.i18n.mail.passwordChanged.title"),
    MAIL_NEW_ACCOUNT_TITLE("vsph.i18n.mail.newAccount.title"),
    ;

    private final String holder;
}
