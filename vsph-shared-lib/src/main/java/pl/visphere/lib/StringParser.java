/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringParser {
    public static char[] parseGuildNameInitials(String guildName) {
        final String[] parts = guildName.split(StringUtils.SPACE);
        final char[] initials;
        if (parts.length > 1) {
            initials = new char[2];
            for (int i = 0; i < 2; i++) {
                initials[i] = parts[i].charAt(0);
            }
        } else {
            initials = new char[]{ parts[0].charAt(0) };
        }
        return initials;
    }
}
