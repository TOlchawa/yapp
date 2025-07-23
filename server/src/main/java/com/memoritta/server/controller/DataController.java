package com.memoritta.server.controller;

import com.memoritta.server.manager.BinaryDataManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class DataController {

    private final BinaryDataManager binaryDataManager;

    @GetMapping("/data/ids")
    @Operation(summary = "List binary data IDs", description = "Returns all keys stored in Redis")
    public java.util.List<String> listIds() {
        log.debug("Listing all Redis IDs");
        return binaryDataManager.listKeys();
    }

    @GetMapping("/data")
    @Operation(summary = "Load binary data", description = "Loads binary data by ID")
    public ResponseEntity<byte[]> loadData(
            @RequestParam
            @Parameter(description = "UUID of the data") String id) {
        byte[] data = binaryDataManager.load(UUID.fromString(id));
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        log.debug("Returning data with id {}", id);
        return ResponseEntity.ok()
                .header("X-Data-Type", "picture")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
