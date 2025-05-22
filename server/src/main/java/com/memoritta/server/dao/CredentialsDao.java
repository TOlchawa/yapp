package com.memoritta.server.dao;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CredentialsDao {
    private String email;
    private String encryptedPassword;
}
