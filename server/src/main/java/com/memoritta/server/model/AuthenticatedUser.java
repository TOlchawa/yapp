package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticatedUser {
    private String jwtToken;
    private User user;
}
