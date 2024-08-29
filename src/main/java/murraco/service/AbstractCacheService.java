package murraco.service;

import com.github.benmanes.caffeine.cache.Cache;
import murraco.manager.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCacheService<K, V> {

    protected RedisTemplate<String, Object> redisTemplate;

    protected CacheManager cacheManager;

    private static final ConcurrentHashMap<String, Cache<?, ?>> cacheMap = new ConcurrentHashMap<>();

    public AbstractCacheService(RedisTemplate<String, Object> redisTemplate, CacheManager cacheManager) {
        this.redisTemplate = redisTemplate;
        this.cacheManager = cacheManager;
    }

    protected Cache<K, V> getOrCreateCache(String cacheName, long cacheSize, long expireAfterWriteMinutes) {
        return (Cache<K, V>) cacheMap.computeIfAbsent(cacheName, key -> cacheManager.createCache(cacheSize, expireAfterWriteMinutes));
    }

    public V getFromCache(K key, String cacheName, long cacheSize, long expireAfterWriteMinutes) {
        Cache<K, V> cache = getOrCreateCache(cacheName, cacheSize, expireAfterWriteMinutes);
        // 1. 从本地缓存中获取
        V value = cacheManager.getFromCache(cache, key);
        if (value != null) {
            return value;
        }

        // 2. 从 Redis 缓存中获取
        value = (V) redisTemplate.opsForValue().get(getRedisKey(key));
        if (value != null) {
            cacheManager.putInCache(cache, key, value);  // 加入本地缓存
            return value;
        }

        // 3. 从数据库中获取
        value = loadFromDatabase(key);
        if (value != null) {
            cacheManager.putInCache(cache, key, value);  // 加入本地缓存
            redisTemplate.opsForValue().set(getRedisKey(key), value);  // 加入 Redis 缓存
        }

        return value;
    }

    protected abstract V loadFromDatabase(K key);

    protected abstract String getRedisKey(K key);

}

