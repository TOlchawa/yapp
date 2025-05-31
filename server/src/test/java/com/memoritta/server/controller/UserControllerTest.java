package com.memoritta.server.controller;

import com.memoritta.server.client.UserRepository;
import com.memoritta.server.dao.UserDao;
import com.memoritta.server.manager.UserAccessManager;
import com.memoritta.server.mapper.UserMapper;
import com.memoritta.server.mapper.UserMapperImpl;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import com.memoritta.server.utils.UserUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {PasswordUtils.class, UserUtils.class, UserController.class, UserAccessManager.class})
class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserController userController;

    @Autowired
    private UserAccessManager userAccessManager;


    @Test
    @DisplayName("createUser should encrypt password and resolve nickname")
    void createUser_shouldEncryptPasswordAndResolveNickname() {
        // Given
        String email = "new" + UUID.randomUUID() + "@example.com";
        String password = "secret";
        String nickname = "OptionalNick";
        String encrypted = passwordUtils.encrypt(password);
        String resolvedNickname = "ResolvedNick";

        UserDao userDao = UserDao.builder()
                .id(UUID.randomUUID())
                .nickname(resolvedNickname)
                .email(email)
                .encryptedPassword(encrypted)
                .build();

        when(userRepository.save(any())).thenReturn(userDao);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty()).thenReturn(Optional.of(userDao));

        // When
        UUID newUserId = userController.createUser(email, password, nickname);
        User user = userController.fetchUser(email, password);

        // Then

        assertThat(newUserId).isNotNull();
        assertThat(user).extracting("id", "nickname", "email")
                .containsExactly(userDao.getId(), resolvedNickname, email);

        verify(userRepository).save(any());
        verify(userRepository, times(2)).findByEmail(anyString());
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
