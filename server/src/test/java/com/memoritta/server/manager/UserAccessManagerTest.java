package com.memoritta.server.manager;

import com.memoritta.server.client.UserRepository;
import com.memoritta.server.controller.UserController;
import com.memoritta.server.dao.UserDao;
import com.memoritta.server.mapper.UserMapper;
import com.memoritta.server.mapper.UserMapperImpl;
import com.memoritta.server.model.Credentials;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import com.memoritta.server.utils.UserUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import({ PasswordUtils.class, UserUtils.class, UserController.class, UserAccessManager.class })
class UserAccessManagerTest {

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAccessManager userAccessManager;

    @Test
    void testValidateCredentials_shouldReturnUser_whenCredentialsAndRetrieveUserMatch() {


        // Given
        String nickname = "testnickname";
        String email = "test@example.com";
        String rawPassword = "secret";
        String encryptedPassword = passwordUtils.encrypt(rawPassword);

        UserDao userDao = UserDao.builder()
                .id(UUID.randomUUID())
                .email(email)
                .nickname(nickname)
                .encryptedPassword(encryptedPassword)
                .build();

        User expectedUser = User.builder()
                .id(userDao.getId())
                .email(userDao.getEmail())
                .nickname(userDao.getNickname())
                .build();

        Credentials credentials = new Credentials();
        credentials.setEmail("email");
        credentials.setEncryptedPassword("rawPassword");

        when(userRepository.save(any())).thenReturn(userDao);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userDao));

        // When
        User result = userAccessManager.authenticateAndFetchUser(email, rawPassword);

        // Then
        assertNotNull(result);
        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getNickname(), result.getNickname());

        verify(userRepository).findByEmail(anyString());

    }

    @Test
    void testSaveUser_shouldSaveUserDaoToRepository() {
        // Given
        String email = "save@example.com";
        String rawPassword = "password";
        String encryptedPassword = passwordUtils.encrypt(rawPassword);
        String nickname = "testNick";

        UserDao userDao = UserDao.builder()
                .id(UUID.randomUUID())
                .email(email)
                .nickname(nickname)
                .encryptedPassword(encryptedPassword)
                .build();

        when(userRepository.save(any())).thenReturn(userDao);

        // When
        UUID userId = userAccessManager.createUser(email, encryptedPassword, nickname);

        // Then
        verify(userRepository).save(any());

        assertThat(userId).isNotNull();
    }

    @Configuration
    public static class Config {
        @Bean
        public UserMapper getUserMapper() {
            return new UserMapperImpl();
        }

        @Bean
        public UserRepository getUserRepository() {
            return mock(UserRepository.class);
        }
    }
}
