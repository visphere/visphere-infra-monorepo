/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements LocaleExtendableSet {
    CREATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.createAccount.res.success"),
    CREATE_ACCOUNT_RESPONSE_ERROR("vsph.i18n.createAccount.res.error"),
    ACTIVATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.activateAccount.res.success"),
    ACTIVATE_ACCOUNT_RESPONSE_ERROR("vsph.i18n.activateAccount.res.error"),
    RESEND_ACTIVATE_ACCOUNT_RESPONSE_SUCCESS("vsph.i18n.resendActivateAccount.res.success"),
    RESEND_ACTIVATE_ACCOUNT_RESPONSE_ERROR("vsph.i18n.resendActivateAccount.res.error");

    private final String holder;
}
