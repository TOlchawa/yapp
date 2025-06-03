package com.memoritta.server.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@Document(collection = "items")
@CompoundIndexes({
        @CompoundIndex(def = "{'description.barcode': 1}", useGeneratedName = true)
})
public class ItemDao {
    @Id
    private UUID id;
    private String name;
    private DescriptionDao description;
    private Map<String, String> tags;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant modifiedAt;
    @CreatedBy
    private UUID createdBy;
}