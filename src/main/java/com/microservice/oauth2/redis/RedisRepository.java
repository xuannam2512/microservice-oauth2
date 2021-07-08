package com.microservice.oauth2.redis;

import com.microservice.oauth2.redis.config.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRepository<T> {

    private final RedisTemplate<String, T> redisTemplate;
    private final RedisProperties redisProperties;

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Optional<T> get(String key, Class<T> resultType) {
        try {
            return Optional.ofNullable(resultType.cast(redisTemplate.opsForValue().get(key)));
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    public Optional<T> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void add(String key, T value, Long expiredTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expiredTime, timeUnit);
    }

    /**
     * Add key/value to redis.
     *
     * @param key         key
     * @param value       Value
     * @param expiredTime ExpiredTime(unit: seconds)
     */
    public void add(String key, T value, Long expiredTime) {
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.SECONDS);
    }

    /**
     * Add key/value to redis, expired time is default value.
     *
     * @param key   key
     * @param value Value
     */
    public void add(String key, T value) {
        redisTemplate.opsForValue().set(key, value, redisProperties.getExpiredTime(), TimeUnit.SECONDS);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void delete(List<String> keys) {
        redisTemplate.delete(keys);
    }

    public void delete(String... keys) {
        redisTemplate.delete(Arrays.asList(keys));
    }
}
