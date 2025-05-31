package com.memoritta.server.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {PasswordUtils.class, UserUtils.class})
class PasswordUtilsTest {

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private UserUtils userUtils;

    @Test
    @DisplayName("Should return hashed password that matches raw input")
    void encrypt_shouldReturnHashedPasswordMatchingRaw() {
        // Given
        String rawPassword = "mySecret123";

        // When
        String hashed = passwordUtils.encrypt(rawPassword);

        // Then
        assertNotNull(hashed, "Hashed password should not be null");
        assertNotEquals(rawPassword, hashed, "Hashed password should not equal raw password");
        assertTrue(new BCryptPasswordEncoder().matches(rawPassword, hashed), "Hashed password should match raw");
    }

    @Test
    @DisplayName("Should return the same nickname when input is not blank")
    void getOrRandomNickName_shouldReturnInputNicknameWhenNotBlank() {
        // Given
        String nickname = "knownUser";

        // When
        String result = userUtils.getOrRandomNickName(nickname);

        // Then
        assertEquals(nickname, result, "Returned nickname should equal input");
    }

    @Test
    @DisplayName("Should generate random nickname when input is blank or null")
    void getOrRandomNickName_shouldGenerateNicknameWhenInputIsBlankOrNull() {
        // Case 1: blank nickname
        String resultBlank = userUtils.getOrRandomNickName("  ");
        assertNotNull(resultBlank, "Nickname should be generated for blank input");
        assertTrue(resultBlank.startsWith("user"), "Generated nickname should start with 'user'");

        // Case 2: null nickname
        String resultNull = userUtils.getOrRandomNickName(null);
        assertNotNull(resultNull, "Nickname should be generated for null input");
        assertTrue(resultNull.startsWith("user"), "Generated nickname should start with 'user'");
    }

    public static class Config {
        @Bean
        PasswordUtils getPasswordUtils() {
            return new PasswordUtils();
        }
    }
}
