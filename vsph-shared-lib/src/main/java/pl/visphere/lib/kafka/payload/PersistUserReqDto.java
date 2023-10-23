/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersistUserReqDto {
    private String username;
    private String password;
    private String emailAddress;
    private String secondEmailAddress;
    private boolean enabledMfa;
}
