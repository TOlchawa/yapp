package com.memoritta.server.controller;

import com.memoritta.server.manager.UserAccessManager;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private PasswordUtils passwordUtils;

    @Mock
    private UserAccessManager userAccessManager;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
    void putUser_shouldSaveAndReturnUser() {
        // Given
        String login = "new@example.com";
        String password = "secret";
        String nickname = "OptionalNick";
        String encrypted = "encryptedXYZ";
        String resolvedNickname = "ResolvedNick";

        User expectedUser = new User();
        expectedUser.setNickname(resolvedNickname);

        when(passwordUtils.encrypt(password)).thenReturn(encrypted);
        when(passwordUtils.getOrRandomNickName(nickname)).thenReturn(resolvedNickname);
        when(userAccessManager.validateCredentials(login, encrypted)).thenReturn(expectedUser);

        // When
        User result = userController.putUser(login, password, nickname);

        // Then
        assertNotNull(result);
        assertEquals(resolvedNickname, result.getNickname());

        verify(passwordUtils).encrypt(password);
        verify(passwordUtils).getOrRandomNickName(nickname);
        verify(userAccessManager).saveUser(login, password, resolvedNickname);
        verify(userAccessManager).validateCredentials(login, encrypted);
    }
}
