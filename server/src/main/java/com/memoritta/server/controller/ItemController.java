package com.memoritta.server.controller;

import com.memoritta.server.client.ItemRepository;
import com.memoritta.server.manager.ItemManager;
import com.memoritta.server.mapper.ItemMapper;
import com.memoritta.server.model.Description;
import com.memoritta.server.model.Item;
import com.memoritta.server.model.PictureOfItem;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemManager itemManager;

    /**
     * Registers a new item with an picture.
     *
     * @param name the name of the item
     * @param note an optional note for the item
     * @param barCode an optional barcode for the item
     * @param picture the picture file of the item
     * @return the UUID of the registered item
     */
    @PostMapping
    public UUID createItem(@RequestParam String name,
                           @RequestParam(required = false) String note,
                           @RequestParam(required = false) String barCode,
                           @RequestParam(required = false) MultipartFile picture) throws IOException {
        return itemManager.saveItem(name, note, barCode, picture);
    }

    /**
     * Fetches an item by its UUID.
     *
     * @para m id the UUID of the item to fetch
     * @return the UUID of the registered item
     */
    @GetMapping
    public Item fetchItem(@RequestParam String id) throws IOException {
        return itemManager.fetchItem(id);
    }


}