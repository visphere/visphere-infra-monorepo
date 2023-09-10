/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.misc.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.moonsphere.lib.i18n.AbstractI18nConfiguration;
import pl.moonsphere.lib.i18n.I18nService;

@Configuration
public class I18nConfiguration extends AbstractI18nConfiguration {
    @Bean
    public I18nService i18nService() {
        return new I18nService(this);
    }
}
