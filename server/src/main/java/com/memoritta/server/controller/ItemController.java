package com.memoritta.server.controller;

import com.memoritta.server.manager.ItemManager;
import com.memoritta.server.model.Item;
import com.memoritta.server.model.SearchSimilarRequest;
import com.memoritta.server.model.TagSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


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

            HttpServletRequest request,

            @RequestParam(required = false)
            @Parameter(description = "Optional picture encoded in Base64")
            String pictureBase64
    ) throws IOException {
        MultipartFile picture = null;
        if (request instanceof MultipartHttpServletRequest multipart) {
            picture = multipart.getFile("picture");
        }
        return itemManager.saveItem(name, note, barCode, picture, pictureBase64);
    }

    @PutMapping("/item/{id}")
    @Operation(
            summary = "Update an item",
            description = "Updates an item. A new picture can be sent as a file or Base64 string."
    )
    public Item updateItem(
            @PathVariable
            @Parameter(description = "UUID of the item to update")
            String id,

            @RequestParam(required = false)
            @Parameter(description = "New name for the item")
            String name,

            @RequestParam(required = false)
            @Parameter(description = "New note for the item")
            String note,

            @RequestParam(required = false)
            @Parameter(description = "New barcode for the item")
            String barCode,

            @RequestParam(required = false)
            @Parameter(description = "New picture encoded in Base64")
            String pictureBase64,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) throws IOException {
        MultipartFile picture = null;
        if (request instanceof MultipartHttpServletRequest multipart) {
            picture = multipart.getFile("picture");
        }
        return itemManager.updateItem(id, name, note, barCode, picture, pictureBase64);
    }

    /**
     * Fetches an item by its UUID.
     *
     * @param id the UUID of the item to fetch
     * @return the full item object
     */
    @GetMapping("/item/{id}")
    @Operation(
            summary = "Fetch a single item by ID",
            description = "Returns the full item object for a given UUID. " +
                    "Useful for retrieving item details by ID after creation or from a list."
    )
    public Item fetchItem(
            @PathVariable
            @Parameter(description = "UUID of the item to retrieve", example = "123e4567-e89b-12d3-a456-426614174000")
            String id
    ) throws IOException {
        return itemManager.fetchItem(id);
    }



    @PostMapping("/items/user")
    @Operation(
            summary = "Get list of all item IDs",
            description = "Returns a list of UUIDs for all items stored in the database."
    )
    public List<UUID> listItems() {
        return itemManager.listAllItemIds();
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

    @PostMapping("/items/tags")
    @Operation(
            summary = "Search items by tags",
            description = "Returns a list of items filtered by tags. Set matchAll to true to require all tags or false to match at least one."
    )
    public List<Item> searchItemsByTags(
            @RequestBody TagSearchRequest request
    ) {
        return itemManager.searchItemsByTags(request.getTags(), request.isMatchAll());
    }

    @PostMapping("/items/barcode")
    @Operation(
            summary = "Get list of item IDs by barcode",
            description = "Returns a list of item UUIDs that share the provided barcode."
    )
    public List<UUID> listItemsByBarcode(
            @RequestParam
            @Parameter(description = "Barcode value to search for", example = "1234567890123")
            String barcode
    ) {
        return itemManager.listItemsByBarcode(barcode);
    }

    @DeleteMapping("/item/{id}")
    @Operation(
            summary = "Delete item",
            description = "Removes an item using its UUID"
    )
    public void deleteItem(
            @PathVariable
            @Parameter(description = "UUID of the item to delete")
            String id
    ) {
        itemManager.deleteItem(id);
    }

}
