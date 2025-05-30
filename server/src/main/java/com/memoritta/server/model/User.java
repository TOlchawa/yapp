package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class User {
    private UUID id;
    private String nickname;
    private String email;
}
