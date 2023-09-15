/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.moonsphere.lib.i18n.ILocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements ILocaleExtendableSet {
    CREATE_ACCOUNT_RESPONSE_SUCCESS("msph.i18n.createAccount.res.success"),
    CREATE_ACCOUNT_RESPONSE_ERROR("msph.i18n.createAccount.res.error"),
    ACTIVATE_ACCOUNT_RESPONSE_SUCCESS("msph.i18n.activateAccount.res.success"),
    ACTIVATE_ACCOUNT_RESPONSE_ERROR("msph.i18n.activateAccount.res.error");

    private final String holder;
}
