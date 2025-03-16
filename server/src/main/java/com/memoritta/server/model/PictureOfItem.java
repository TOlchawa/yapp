package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PictureOfItem {
    private UUID id;
    private byte[] picture;
    private String metadata;
}
