package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class AuthenticatedUser {
    private String jwtToken;
    private User user;
}
