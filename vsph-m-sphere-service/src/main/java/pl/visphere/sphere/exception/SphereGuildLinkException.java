/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.sphere.i18n.LocaleSet;

import java.util.Map;

public class SphereGuildLinkException {
    @Slf4j
    public static class SphereGuildLinkNotFoundException extends AbstractRestException {
        public SphereGuildLinkNotFoundException(Long linkId) {
            super(HttpStatus.NOT_FOUND, LocaleSet.SPHERE_GUILD_LINK_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE, Map.of(
                "linkId", linkId
            ));
            log.error("Searching sphere guild for id: '{}' not found in database.", linkId);
        }
    }
}
