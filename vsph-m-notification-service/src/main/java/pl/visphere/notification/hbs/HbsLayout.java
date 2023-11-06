/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.hbs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HbsLayout implements HbsFile {
    MAIN_LAYOUT("main-layout"),
    ;

    private final String layoutName;

    @Override
    public String getPath(HbsProperties hbsProperties) {
        return hbsProperties.getLayoutsPath() + "/" + layoutName + hbsProperties.getExtension();
    }
}
