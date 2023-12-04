/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class CacheService {
    private final CacheManager cacheManager;

    public <T> Optional<T> getSafetyFromCache(
        AppCache appCache, Object key, Class<T> clazz, Supplier<Optional<T>> notPresent
    ) {
        final Cache cache = getCache(appCache);
        if (cache != null) {
            Optional<T> cached = Optional.ofNullable(cache.get(key, clazz));
            if (cached.isPresent()) {
                log.info("Getting value from cache: '{}' with key: '{}'", cached.get(), key);
                return cached;
            }
            cached = notPresent.get();
            if (cached.isEmpty()) {
                return Optional.empty();
            }
            log.info("Persisted value in cache: '{}' with key: '{}'", cached.get(), key);
            cache.put(key, cached.get());
            return cached;
        }
        return Optional.empty();
    }

    public void updateCache(AppCache appCache, Object key, Object persistedObj) {
        final Cache cache = getCache(appCache);
        if (cache != null) {
            cache.evict(key);
            cache.put(key, persistedObj);
        }
    }

    private Cache getCache(AppCache appCache) {
        return cacheManager.getCache(appCache.getName());
    }
}
