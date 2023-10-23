/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@RequiredArgsConstructor
public class KafkaLocaleTransporter implements LocaleExtendableSet {
    private final String placeholder;
    
    @Override
    public String getHolder() {
        return placeholder;
    }
}
