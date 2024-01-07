/*
 * Copyright (c) 2024 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class StringParserTest {
    @Test
    void parseGuildNameInitials() {
        // given
        final String[] mockedNames = { "testguild123", "test test123", "GGD2" };
        final String[] expectedResults = { "t", "tt", "G" };

        // when
        final String[] actualResults = new String[expectedResults.length];
        for (int i = 0; i < mockedNames.length; i++) {
            actualResults[i] = new String(StringParser.parseGuildNameInitials(mockedNames[i]));
        }

        // then
        assertArrayEquals(expectedResults, actualResults);
    }
}
