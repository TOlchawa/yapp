package com.memoritta.server.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class PasswordUtils {

    private Random random = new Random();

    public String encrypt(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public String getOrRandomNickName(String nickname) {
        String result = nickname;
        if (isBlank(result)) {
            result = "user" + ( System.currentTimeMillis() % 1000000 ) + "-" + random.nextInt(1000000);
        }
        return result;
    }
}
