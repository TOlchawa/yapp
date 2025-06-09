package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class Item {
    private UUID id;
    private String name;
    private Description description;
    private Map<String, String> tags;
}