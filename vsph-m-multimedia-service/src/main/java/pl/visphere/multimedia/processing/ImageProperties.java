/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.processing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@RefreshScope
@ConfigurationProperties("visphere.generator.image")
public class ImageProperties {
    private String defaultColor;
    private List<String> colors;
    private String fontName;
    private String fontPath;
    private int fontSize;
    private int size;
    private int maxSizeMb;
}
