package com.memoritta.server.dao;

import com.memoritta.server.model.Description;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ItemDao {
    private UUID id;
    private UUID createdBy;
    private String name;
    private DescriptionDao description;
}