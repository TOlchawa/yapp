package com.memoritta.server.controller;

import com.memoritta.server.client.UserRepository;
import com.memoritta.server.config.PasswordEncoderConfig;
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
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
//@ContextConfiguration(classes = { UserControllerTest.Config.class, PasswordEncoderConfig.class })
@Import({ PasswordEncoderConfig.class, PasswordUtils.class, UserUtils.class, UserController.class, UserAccessManager.class })
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
        String email = "new@example.com";
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
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userDao));

        // When
        UUID newUserId = userController.createUser(email, password, nickname);
        User user = userController.fetchUser(email, password);

        // Then

        assertThat(newUserId).isNotNull();
        assertThat(user).extracting("id", "nickname", "email")
                .containsExactly(userDao.getId(), resolvedNickname, email);
    }

    @Configuration
    public static class Config {
//        @Bean
//        public UserAccessManager getUserAccessManager() {
//            return mock(UserAccessManager.class);
//        }

        @Bean
        public UserMapper getUserMapper() {
            return new UserMapperImpl();
        }

//        @Bean
//        public PasswordUtils getPasswordUtils(BCryptPasswordEncoder encoder) {
//            return new PasswordUtils(encoder);
//        }
//
//        @Bean
//        public UserUtils getUserUtils() {
//            return new UserUtils();
//        }

//        @Bean
//        public UserController getUserController(PasswordUtils passwordUtils, UserUtils userUtils, UserAccessManager userAccesManager) {
//            return new UserController(passwordUtils, userUtils, userAccesManager);
//        }
//
//        @Bean
//        public UserAccessManager getuserAccessManager(UserMapper userMapper, PasswordUtils passwordUtils, UserRepository userRepository) {
//            return new UserAccessManager(userMapper, passwordUtils, userRepository);
//        }


        @Bean
        public UserRepository getUserRepository() {
            return mock(UserRepository.class);
        }

//        @Bean
//        public PasswordEncoderConfig getPasswordEncoderConfig() {
//            return new PasswordEncoderConfig();
//        }

    }
}
