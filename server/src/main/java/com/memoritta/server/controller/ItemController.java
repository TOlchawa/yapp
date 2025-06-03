package com.memoritta.server.controller;

import com.memoritta.server.manager.ItemManager;
import com.memoritta.server.model.Item;
import com.memoritta.server.model.SearchSimilarRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping
public class ItemController {

    private final ItemManager itemManager;

    @PostMapping("/item")
    @Operation(
            summary = "Register a new item with optional image",
            description = "Registers a new item using its name, optional note, optional barcode, and an optional picture. " +
                    "The endpoint returns the UUID of the newly created item."
    )
    public UUID createItem(
            @RequestParam
            @Parameter(description = "Name of the item", required = true, example = "Notebook")
            String name,

            @RequestParam(required = false)
            @Parameter(description = "Optional note about the item", example = "This is a test item")
            String note,

            @RequestParam(required = false)
            @Parameter(description = "Optional barcode of the item", example = "1234567890123")
            String barCode,

            @RequestParam(required = false)
            @Parameter(description = "Optional picture file of the item (image/jpeg or image/png)")
            MultipartFile picture
    ) throws IOException {
        return itemManager.saveItem(name, note, barCode, picture);
    }

    /**
     * Fetches an item by its UUID.
     *
     * @param id the UUID of the item to fetch
     * @return the full item object
     */
    @GetMapping("/item")
    @Operation(
            summary = "Fetch a single item by ID",
            description = "Returns the full item object for a given UUID. " +
                    "Useful for retrieving item details by ID after creation or from a list."
    )
    public Item fetchItem(
            @RequestParam
            @Parameter(description = "UUID of the item to retrieve", example = "123e4567-e89b-12d3-a456-426614174000")
            String id
    ) throws IOException {
        return itemManager.fetchItem(id);
    }


    @PostMapping("/items/user")
    @Operation(
            summary = "Get list of item IDs by user email",
            description = "Returns a list of item UUIDs that were created by the user with the provided email address. " +
                    "Note: This version uses email as a parameter, but support for JWT authentication will be added later."
    )
    public List<UUID> listItems(
            @RequestParam
            @Parameter(description = "Email address of the user whose items should be returned", example = "user@example.com")
            String email
    ) {
        List<UUID> listItems = itemManager.listItems(email); // TODO add support for JWT
        return listItems;
    }

    @PostMapping("/items/search")
    @Operation(
            summary = "Search items similar to the given item ID",
            description = "Returns a list of items similar to the provided ID. Currently returns a single item list with the requested item."
    )
    public List<Item> searchSimilarItems(
            @RequestBody
            @Parameter(description = "Search parameters containing the item ID")
            SearchSimilarRequest request
    ) {
        return itemManager.searchSimilarItems(request.getId());
    }

}