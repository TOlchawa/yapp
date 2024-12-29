package com.memoritta.server.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Description {
    private String note;
    private String barcode;
    private List<PictureOfItem> pictures;
}
