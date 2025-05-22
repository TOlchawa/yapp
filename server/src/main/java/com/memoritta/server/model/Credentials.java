package com.memoritta.server.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Setter
@Getter
public class Credentials {
    private String email;
    private String encryptedPassword;
}
