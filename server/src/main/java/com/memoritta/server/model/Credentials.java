package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Credentials {
    private String email;
    private String password;
}
