package com.memoritta.server.manager;

import com.memoritta.server.model.Item;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ItemManager {
    private final RedisTemplate<String, String> redisTemplate;
    private final BinaryDataManager binaryDataManager;

    public UUID save(Item item) {
        // TODO: add implementation
        return UUID.randomUUID();
    }

    public Item load(UUID id) {
        // TODO: add implementation
        return null;
    }

}
