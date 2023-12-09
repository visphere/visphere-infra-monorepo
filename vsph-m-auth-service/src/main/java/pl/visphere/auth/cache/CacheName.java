/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.cache.AppCache;

@Getter
@RequiredArgsConstructor
public enum CacheName implements AppCache {
    USER_ENTITY_USER_ID("user_entity_user_id"),
    ;

    private final String name;
}
