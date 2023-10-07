/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.visphere.lib.i18n.AbstractI18nConfiguration;
import pl.visphere.lib.i18n.I18nService;

@Configuration
class I18nConfiguration extends AbstractI18nConfiguration {
    @Bean
    I18nService i18nService() {
        return new I18nService(this);
    }
}
