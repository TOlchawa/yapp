package com.memoritta.server.manager;

import com.memoritta.server.client.ItemRepository;
import com.memoritta.server.client.UserRepository;
import com.memoritta.server.dao.ItemDao;
import com.memoritta.server.dao.UserDao;
import com.memoritta.server.mapper.ItemMapper;
import com.memoritta.server.model.Description;
import com.memoritta.server.model.Item;
import com.memoritta.server.model.PictureOfItem;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ItemManager {

    private final UserRepository userRepository; // TODO: add support for JWT and remove this dependency (repository is not needed for JWT)
    private final ItemRepository itemRepository;
    private final BinaryDataManager binaryDataManager;

    public UUID saveItem(String name,
                        String note,
                        String barCode,
                        MultipartFile picture,
                        String pictureBase64) throws IOException {
        Item item = Item.builder()
                .name(name)
                .id(UUID.randomUUID())
                .build();

        Description description = null;

        // Create description if any optional field was provided
        if (picture != null || pictureBase64 != null || note != null || barCode != null) {
            description = Description.builder().build();

            if (picture != null || pictureBase64 != null) {
                byte[] imageBytes;
                if (picture != null) {
                    imageBytes = picture.getBytes();
                } else {
                    imageBytes = Base64.getDecoder().decode(pictureBase64);
                }
                PictureOfItem pictureOfItem = PictureOfItem.builder().build();
                pictureOfItem.setPicture(imageBytes);
                description.setPictures(List.of(pictureOfItem));
            }

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
                log.debug("Saving picture for item {} to Redis", item.getId());
                UUID pictureId = binaryDataManager.save(picture.getPicture());
                picture.setId(pictureId);
                picture.setPicture(null);
            }
        }

        log.info("Saving item: {}", item);
        ItemDao itemDao = itemRepository.save(ItemMapper.INSTANCE.toItemDao(item));

        UUID id = itemDao.getId();
        item.setId(id);
        return id;
    }

    public Item fetchItem(String id) {
        return itemRepository.findById(UUID.fromString(id))
                .map(ItemMapper.INSTANCE::toItem)
                .orElse(null);
    }

    public Item updateItem(String id,
                           String name,
                           String note,
                           String barCode,
                           MultipartFile picture,
                           String pictureBase64) throws IOException {
        Item item = fetchItem(id);
        if (item == null) {
            throw new IllegalArgumentException("Item not found with id: " + id);
        }

        if (name != null) {
            item.setName(name);
        }

        boolean hasUpdate = note != null || barCode != null || picture != null || pictureBase64 != null;
        Description description = item.getDescription();
        if (description == null && hasUpdate) {
            description = Description.builder().build();
            item.setDescription(description);
        }

        if (description != null) {
            if (note != null) {
                description.setNote(note);
            }
            if (barCode != null) {
                description.setBarcode(barCode);
            }

            if (picture != null || pictureBase64 != null) {
                byte[] data = null;
                if (picture != null) {
                    data = picture.getBytes();
                } else {
                    data = Base64.getDecoder().decode(pictureBase64);
                }
                PictureOfItem newPic = PictureOfItem.builder().build();
                newPic.setPicture(data);
                description.setPictures(List.of(newPic));
            }
        }

        save(item);
        return item;
    }

    public List<Item> searchSimilarItems(String id) {
        Item item = fetchItem(id);
        if (item == null) {
            return List.of();
        }
        return List.of(item);
    }


    public List<UUID> listAllItemIds() {
        List<ItemDao> items = itemRepository.findAll();
        return items.stream()
                .map(ItemDao::getId)
                .toList();
    }

    public List<Item> searchItemsByTags(List<String> tags, boolean matchAll) {
        List<ItemDao> all = itemRepository.findAll();

        Map<String, String> searchTags = tags.stream()
                .map(t -> t.startsWith("#") ? t.substring(1) : t)
                .map(t -> t.split("=", 2))
                .collect(java.util.stream.Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));

        return all.stream()
                .filter(dao -> {
                    Map<String, String> itemTags = dao.getTags();
                    if (itemTags == null || itemTags.isEmpty()) {
                        return false;
                    }

                    if (matchAll) {
                        return searchTags.entrySet().stream()
                                .allMatch(e -> e.getValue().equals(itemTags.get(e.getKey())));
                    } else {
                        return searchTags.entrySet().stream()
                                .anyMatch(e -> e.getValue().equals(itemTags.get(e.getKey())));
                    }
                })
                .map(ItemMapper.INSTANCE::toItem)
                .toList();
    }

    public List<UUID> listItemsByBarcode(String barcode) {
        List<ItemDao> items = itemRepository.findByDescriptionBarcode(barcode);
        return items.stream()
                .map(ItemDao::getId)
                .toList();
    }

    /**
     * Delete an item by its ID.
     *
     * @param id the item UUID as string
     */
    public void deleteItem(String id) {
        itemRepository.deleteById(UUID.fromString(id));
    }
}
