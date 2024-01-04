/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements LocaleExtendableSet {
    MESSAGE_DELETED_RESPONSE_SUCCESS("vsph.i18n.messageDeleted.res.success"),
    MESSAGE_NOT_FOUND_EXCEPTION_MESSAGE("vsph.chat.exc.messageNotFound"),
    ;

    private final String holder;
}
