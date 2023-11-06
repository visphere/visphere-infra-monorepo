/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.hbs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HbsTemplate implements HbsFile {
    ACTIVATE_ACCOUNT("activate-account"),
    NEW_ACCOUNT("new-account"),
    CHANGE_PASSWORD("change-password"),
    PASSWORD_CHANGED("password-changed"),
    ;

    private final String templateName;
    private final HbsLayout layout;

    HbsTemplate(String templateName) {
        this.templateName = templateName;
        this.layout = HbsLayout.MAIN_LAYOUT;
    }

    @Override
    public String getPath(HbsProperties hbsProperties) {
        return hbsProperties.getTemplatesPath() + "/" + templateName + hbsProperties.getExtension();
    }
}