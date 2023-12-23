/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.joinguild.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JoiningGuildDetailsResDto {
    private long id;
    private String name;
    private String category;
    private int participants;
    private boolean isPrivate;
    private String profileColor;
    private String profileImageUrl;
}
