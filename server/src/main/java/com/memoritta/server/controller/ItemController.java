package com.memoritta.server.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ItemController {

    /**
     * Registers a new item without an image.
     *
     * @param name the name of the item
     * @return a confirmation message
     */
    @PostMapping("/registerItem")
    public String registerItem(@RequestParam String name) {
        // Logic to register item without image
        return "Item registered: " + name;
    }

    /**
     * Registers a new item with an image.
     *
     * @param name the name of the item
     * @param image the image file of the item
     * @return a confirmation message
     */
    @PostMapping("/registerItemWithImage")
    public String registerItemWithImage(@RequestParam String name, @RequestParam(required = false) MultipartFile image, @RequestParam(required = false) String note) {
        // Logic to register item with image
        return "Item registered image: " + name + " and note: " + note + " and image";
    }


}