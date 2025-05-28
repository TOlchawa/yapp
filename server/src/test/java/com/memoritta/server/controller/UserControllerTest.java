package com.memoritta.server.controller;

import com.memoritta.server.client.UserRepository;
import com.memoritta.server.manager.UserAccessManager;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import com.memoritta.server.utils.UserUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import({ UserControllerTest.Config.class, UserController.class, PasswordUtils.class, UserUtils.class })
class UserControllerTest {

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserController userController;

    @Autowired
    private UserAccessManager userAccessManager;


    @Test
    void getUser_shouldReturnUser_whenCredentialsAreValid() {
        // Given
        String login = "test@example.com";
        String password = "plaintext";
        String encrypted = "encrypted123";

        User expectedUser = new User();
        expectedUser.setNickname("Tester");

        when(passwordUtils.encrypt(password)).thenReturn(encrypted);
        when(userAccessManager.validateCredentials(login, encrypted)).thenReturn(expectedUser);

        // When
        User result = userController.getUser(login, password);

        // Then
        assertNotNull(result);
        assertEquals("Tester", result.getNickname());

        verify(passwordUtils).encrypt(password);
        verify(userAccessManager).validateCredentials(login, encrypted);
    }

    @Test
    @DisplayName("putUser should save and return user with resolved nickname")
    void putUser_shouldSaveAndReturnUser() {
        // Given
        String email = "new@example.com";
        String password = "secret";
        String nickname = "OptionalNick";
        String encrypted = "encryptedXYZ";
        String resolvedNickname = "ResolvedNick";

        User expectedUser = new User();
        expectedUser.setNickname(resolvedNickname);

        when(userAccessManager.validateCredentials(email, encrypted)).thenReturn(expectedUser);

        // When

        User result = userController.putUser(email, password, nickname);

        // Then

        assertThat(result).extracting("id", "nickname", "email")
                .containsExactly(expectedUser.getId(), resolvedNickname, email);
    }

    @Configuration
    public static class Config {
        @Bean
        UserAccessManager getUserAccessManager() {
            return mock(UserAccessManager.class);
        }

    }
}
