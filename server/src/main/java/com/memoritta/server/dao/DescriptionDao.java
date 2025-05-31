package com.memoritta.server.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Document(collection = "descriptions")
public class DescriptionDao {
    @Id
    private UUID id;
    private String note;
    @Indexed
    private String barcode;
    private List<PictureOfItemDao> pictures;
}
