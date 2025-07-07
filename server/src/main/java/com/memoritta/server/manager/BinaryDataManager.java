package com.memoritta.server.manager;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BinaryDataManager {

    private final RedisTemplate<String, byte[]> redisTemplate;

    public UUID save(byte[] data) {
        UUID id = UUID.randomUUID();
        redisTemplate.opsForValue().set(id.toString(), data);
        return id;
    }

    public byte[] load(UUID id) {
        return redisTemplate.opsForValue().get(id.toString());
    }

    /**
     * Get all keys stored in Redis.
     *
     * @return list of keys as strings
     */
    public java.util.List<String> listKeys() {
        java.util.Set<String> keys = redisTemplate.keys("*");
        if (keys == null) {
            return java.util.List.of();
        }
        return new java.util.ArrayList<>(keys);
    }
}
