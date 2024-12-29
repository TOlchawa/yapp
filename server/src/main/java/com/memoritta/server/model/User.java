package com.memoritta.server.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private UUID id;
    private String name;
    private Credentials credentials;
}
