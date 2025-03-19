package com.memoritta.server.manager;

import com.memoritta.server.model.Description;
import com.memoritta.server.model.Item;
import com.memoritta.server.model.PictureOfItem;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ItemManager {
    private final RedisTemplate<String, String> redisTemplate;
    private final BinaryDataManager binaryDataManager;

    public UUID save(Item item) {
        Description description = item.getDescription();
        if (description != null) {
            List<PictureOfItem> pictures = description.getPictures();
            if (pictures != null && !pictures.isEmpty()) {
                PictureOfItem picture = pictures.get(0);
                UUID pictureId = binaryDataManager.save(picture.getPicture());
                picture.setId(pictureId);
            }
        }
        // TODO: map item to json and save in mongodb or something similar maybe something like neo4j
        item.setId(UUID.randomUUID());
        return item.getId();
    }

    public Item load(UUID id) {
        // TODO: add implementation
        return null;
    }

}
