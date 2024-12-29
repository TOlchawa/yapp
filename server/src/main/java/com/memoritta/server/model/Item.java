package com.memoritta.server.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Item {
    private UUID id;
    private String name;
    private Description desc;
}