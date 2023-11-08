/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.check.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyAccountResDto {
    private String accountId;
    private String usernameOrEmailAddress;
    private String thumbnailUrl;
    private boolean verified;
}
