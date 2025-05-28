package com.memoritta.server.manager;

import com.memoritta.server.client.UserRepository;
import com.memoritta.server.config.PasswordEncoderConfig;
import com.memoritta.server.dao.UserDao;
import com.memoritta.server.mapper.UserMapper;
import com.memoritta.server.model.Credentials;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = { UserAccessManagerTest.Config.class, PasswordEncoderConfig.class } )
class UserAccessManagerTest {

    @Autowired
    private PasswordUtils passwordUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserAccessManager userAccessManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateCredentials_shouldReturnUser_whenCredentialsMatch() {


        // Given
        String nickname = "testnickname";
        String email = "test@example.com";
        String rawPassword = "secret";
        String encryptedPassword = passwordUtils.encrypt(rawPassword);

        UserDao userDao = new UserDao();
        userDao.setEmail(email);
        userDao.setNickname(nickname);
        userDao.setEncryptedPassword(encryptedPassword);

        User expectedUser = new User();
        expectedUser.setId(UUID.randomUUID());
        expectedUser.setNickname("nickname");
        Credentials credentials = new Credentials();
        credentials.setEmail("email");
        credentials.setEncryptedPassword("encryptedpassword");

        when(passwordUtils.encrypt(rawPassword)).thenReturn(encryptedPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userDao));
        when(userMapper.toUser(userDao)).thenReturn(expectedUser);

        // When
        User result = userAccessManager.validateCredentials(email, rawPassword);

        // Then
        assertNotNull(result);
        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getNickname(), result.getNickname());

        verify(passwordUtils).encrypt(rawPassword);
        verify(userRepository).findByEmail(email);
        verify(userMapper).toUser(userDao);
    }

    @Test
    void testSaveUser_shouldSaveUserDaoToRepository() {
        // Given
        String email = "save@example.com";
        String rawPassword = "password";
        String encryptedPassword = passwordUtils.encrypt(rawPassword);
        String nickname = "testNick";

        ArgumentCaptor<UserDao> captor = ArgumentCaptor.forClass(UserDao.class);

        // When
        UUID userId = userAccessManager.createUser(email, encryptedPassword, nickname);

        // Then
        verify(userRepository).save(captor.capture());
        UserDao savedUserDao = captor.getValue();

        assertThat(savedUserDao.getEmail()).isEqualTo(email);
        assertThat(savedUserDao.getNickname()).isEqualTo(nickname);
        assertThat(savedUserDao.getEncryptedPassword()).isEqualTo(encryptedPassword);
        passwordUtils.verifyPassword(encryptedPassword, rawPassword);
    }

    public static class Config {
        @Bean
        public PasswordUtils getPasswordUtils(BCryptPasswordEncoder encoder) {
            return new PasswordUtils(encoder);
        }
    }
}
