/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.visphere.multimedia.processing.drawer.IdenticonDrawer;
import pl.visphere.multimedia.processing.drawer.ImageDrawer;
import pl.visphere.multimedia.processing.drawer.InitialsDrawer;

@Configuration
@RequiredArgsConstructor
public class ProcessingBeansConfiguration {
    private final ImageProperties imageProperties;

    @Bean
    IdenticonDrawer identiconDrawer() {
        return new IdenticonDrawer(imageProperties);
    }

    @Bean
    InitialsDrawer initialsDrawer() {
        return new InitialsDrawer(imageProperties);
    }

    @Bean
    ImageDrawer imageDrawer() {
        return new ImageDrawer(imageProperties);
    }
}
