/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.multimedia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@AllArgsConstructor
public class ProfileImageDetailsResDto {
    private String profileColor;
    private String profileImageUuid;
    private String profileImagePath;
    private String credentialsSupplier;
    private boolean isCustomImage;

    public ProfileImageDetailsResDto() {
        this.profileColor = StringUtils.EMPTY;
        this.profileImageUuid = StringUtils.EMPTY;
        this.profileImagePath = StringUtils.EMPTY;
        this.credentialsSupplier = "local";
        this.isCustomImage = true;
    }
}
