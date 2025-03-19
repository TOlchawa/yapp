package com.memoritta.server.dao;

import com.memoritta.server.model.PictureOfItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class DescriptionDao {
    private UUID id;
    private String note;
    private String barcode;
    private List<PictureOfItemDao> pictures;
}
