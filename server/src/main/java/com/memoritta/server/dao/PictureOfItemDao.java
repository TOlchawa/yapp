package com.memoritta.server.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PictureOfItemDao {
    private UUID id;
    private byte[] picture;
    private String metadata;
}
