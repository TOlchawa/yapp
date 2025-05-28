package com.memoritta.server.utils;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PasswordUtils {

    private BCryptPasswordEncoder encoder;

    public String encrypt(String password) {
        return encoder.encode(password);
    }

    public void verifyPassword(String encryptedPassword, String rawPassword) {
        if (!encoder.matches(rawPassword, encryptedPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }
    }
}
