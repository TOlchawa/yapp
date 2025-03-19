package com.memoritta.server.controller;

import com.memoritta.server.client.ItemRepository;
import com.memoritta.server.manager.ItemManager;
import com.memoritta.server.mapper.ItemMapper;
import com.memoritta.server.model.Description;
import com.memoritta.server.model.Item;
import com.memoritta.server.model.PictureOfItem;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ItemController {

    private final ItemManager itemManager;
    private final ItemRepository itemRepository;

    /**
     * Registers a ping.
     *
     * @return the UUID of the registered item
     */
    @SneakyThrows
    @PostMapping("/ping")
        public UUID registerItemWithImage(@RequestParam String ping) {
        return UUID.randomUUID();
    }


    /**
     * Registers a new item with an picture.
     *
     * @param name the name of the item
     * @param note an optional note for the item
     * @param barCode an optional barcode for the item
     * @param picture the picture file of the item
     * @return the UUID of the registered item
     */
    @SneakyThrows
    @PostMapping("/registerItem")
    // TODO: add support for authentication
    public UUID registerItemWithImage(@RequestParam String name,
                                      @RequestParam(required = false) String note,
                                      @RequestParam(required = false) String barCode,
                                      @RequestBody(required = false) MultipartFile picture) {
        return saveItem(name, note, barCode, picture);
    }

    private UUID saveItem(String name, String note, String barCode, MultipartFile picture) throws IOException {
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
        }
        if (note != null) {
            if (description == null) {
                description = Description.builder().build();
            }
            description.setNote(note);
        }
        if (barCode != null) {
            if (description == null) {
                description = Description.builder().build();
            }
            description.setBarcode(barCode);
        }
        if (description != null) {
            item.setDescription(description);
        }

        // TODO: make it proper - this is only PoC
        UUID id = itemManager.save(item);
        itemRepository.save(ItemMapper.INSTANCE.toItemDao(item));
        return id;
        // TODO: make it proper - this is only PoC
    }


}