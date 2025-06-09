package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class Description {
    private UUID id;
    private String note;
    private String barcode;
    private List<PictureOfItem> pictures;
}
