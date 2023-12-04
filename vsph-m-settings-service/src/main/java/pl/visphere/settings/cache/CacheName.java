/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.cache.AppCache;

@Getter
@RequiredArgsConstructor
public enum CacheName implements AppCache {
    USER_RELATION_MODEL_USER_ID("user_relation_model_user_id"),
    ;

    private final String name;
}
