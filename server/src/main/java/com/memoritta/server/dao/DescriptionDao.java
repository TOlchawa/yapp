package com.memoritta.server.dao;

import com.memoritta.server.model.PictureOfItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class DescriptionDao {
    @Id
    private UUID id;
    private String note;
    @Indexed
    private String barcode;
    private List<PictureOfItemDao> pictures;
}
