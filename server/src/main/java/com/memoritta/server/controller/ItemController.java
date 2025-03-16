package com.memoritta.server.controller;

import com.memoritta.server.manager.ItemManager;
import com.memoritta.server.model.Description;
import com.memoritta.server.model.Item;
import com.memoritta.server.model.PictureOfItem;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ItemController {

    private final ItemManager itemManager;

    /**
     * Registers a new item with an image.
     *
     * @param name the name of the item
     * @param note an optional note for the item
     * @param barCode an optional barcode for the item
     * @param imageFile the image file of the item
     * @return the UUID of the registered item
     */
    @SneakyThrows
    @PostMapping("/registerItem")
    public UUID registerItemWithImage(@RequestParam String name, @RequestParam(required = false) String note, @RequestParam(required = false) String barCode, @RequestParam(required = false) MultipartFile imageFile) {
        Item item = Item.builder()
                .name(name)
                .id(UUID.randomUUID())
                .build();
        Description description = null;
        if (imageFile != null) {
            description = Description.builder().build();
            byte[] imageBytes = imageFile.getBytes();
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

        return itemManager.save(item);
    }


}