package com.memoritta.server.manager;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class BinaryDataManager {

    private final RedisTemplate<String, byte[]> redisTemplate;

    public UUID save(byte[] data) {
        UUID id = UUID.randomUUID();
        log.debug("Saving data with id {} to Redis", id);
        redisTemplate.opsForValue().set(id.toString(), data);
        return id;
    }

    public byte[] load(UUID id) {
        log.debug("Loading data with id {} from Redis", id);
        return redisTemplate.opsForValue().get(id.toString());
    }

    /**
     * Get all keys stored in Redis.
     *
     * @return list of keys as strings
     */
    public java.util.List<String> listKeys() {
        log.debug("Listing all keys in Redis");
        java.util.Set<String> keys = redisTemplate.keys("*");
        if (keys == null) {
            return java.util.List.of();
        }
        return new java.util.ArrayList<>(keys);
    }
}
