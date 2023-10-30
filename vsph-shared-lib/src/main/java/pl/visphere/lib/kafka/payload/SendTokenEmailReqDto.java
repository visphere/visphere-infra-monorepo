/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class SendTokenEmailReqDto {
    private String fullName;
    private String username;
    private String emailAddress;
    private String secondEmailAddress;
    private String otaToken;
    private ZonedDateTime expiredAt;
}
