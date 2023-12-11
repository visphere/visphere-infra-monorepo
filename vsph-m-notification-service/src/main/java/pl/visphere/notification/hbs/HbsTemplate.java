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
    MFA_CODE("mfa-code"),
    UPDATED_MFA_STATE("updated-mfa-state"),
    RESET_MFA_STATE("reset-mfa-state"),
    REQ_UPDATE_EMAIL("req-update-email"),
    REQ_UPDATE_SECOND_EMAIL("req-update-second-email"),
    UPDATED_EMAIL("updated-email"),
    UPDATED_SECOND_EMAIL("updated-second-email"),
    REMOVED_SECOND_EMAIL("removed-second-email"),
    ENABLED_ACCOUNT("enabled-account"),
    DISABLED_ACCOUNT("disabled-account"),
    DELETED_ACCOUNT("deleted-account"),
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
