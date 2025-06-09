package com.memoritta.server.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordUtils {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encrypt(String password) {
        return encoder.encode(password);
    }

    public void verifyPassword(String encryptedPassword, String rawPassword) {
        if (!encoder.matches(rawPassword, encryptedPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }
    }

}
