package com.memoritta.server.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Getter
@Setter
@Builder
@Document(collection = "pictures")
@NoArgsConstructor
public class PictureOfItemDao {
    @Id
    private UUID id;
    private byte[] picture;
    @Indexed
    private String metadata;
}
