package murraco.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CacheManager {

    public <K, V> Cache<K, V> createCache(long maxSize, long expireAfterWriteMinutes) {
        return Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWriteMinutes, TimeUnit.MINUTES)
                .maximumSize(maxSize)
                .build();
    }

    public <K, V> V getFromCache(Cache<K, V> cache, K key) {
        return cache.getIfPresent(key);
    }

    public <K, V> void putInCache(Cache<K, V> cache, K key, V value) {
        cache.put(key, value);
    }

    public <K, V> void invalidateCache(Cache<K, V> cache, K key) {
        cache.invalidate(key);
    }
}
