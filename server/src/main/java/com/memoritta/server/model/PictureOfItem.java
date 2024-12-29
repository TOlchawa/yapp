package com.memoritta.server.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PictureOfItem {
    private byte[] picture;
    private String metadata;
}
