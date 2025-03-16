package com.memoritta.server.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class User {
    private UUID id;
    private String name;
    private Credentials credentials;
}
