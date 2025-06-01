package com.memoritta.server.manager;

import com.memoritta.server.client.ItemRepository;
import com.memoritta.server.client.UserRepository;
import com.memoritta.server.dao.ItemDao;
import com.memoritta.server.dao.UserDao;
import com.memoritta.server.mapper.ItemMapper;
import com.memoritta.server.model.Description;
import com.memoritta.server.model.Item;
import com.memoritta.server.model.PictureOfItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ItemManager {

    private final UserRepository userRepository; // TODO: add support for JWT and remove this dependency (repository is not needed for JWT)
    private final ItemRepository itemRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final BinaryDataManager binaryDataManager;

    public UUID saveItem(String name, String note, String barCode, MultipartFile picture) throws IOException {
        Item item = Item.builder()
                .name(name)
                .id(UUID.randomUUID())
                .build();
        Description description = null;
        if (picture != null) {
            description = Description.builder().build();
            byte[] imageBytes = picture.getBytes();
            PictureOfItem pictureOfItem = PictureOfItem.builder().build();
            pictureOfItem.setPicture(imageBytes);
            description.setPictures(List.of(pictureOfItem));
            description.setNote(note);
            description.setBarcode(barCode);
            item.setDescription(description);
        }
        UUID id = save(item);

        return id;
    }

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

        log.info("Saving item: {}", item);
        ItemDao itemDao = itemRepository.save(ItemMapper.INSTANCE.toItemDao(item));

        UUID id = itemDao.getId();
        item.setId(id);
        return id;
    }

    public Item fetchItem(String id) {
        return null;
    }

    public List<UUID> listItems(String email) {
        UserDao user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        UUID userId = user.getId();
        List<ItemDao> items = itemRepository.findByCreatedBy(userId);
        return items.stream()
                .map(ItemDao::getId)
                .toList();
    }
}
