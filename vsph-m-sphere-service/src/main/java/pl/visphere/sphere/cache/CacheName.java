/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.cache.AppCache;

@Getter
@RequiredArgsConstructor
public enum CacheName implements AppCache {
    GUILD_IDS_USER_ID("guild_ids_user_id"),
    GUILD_CATEGORIES("guild_categories"),
    ;

    private final String name;
}
