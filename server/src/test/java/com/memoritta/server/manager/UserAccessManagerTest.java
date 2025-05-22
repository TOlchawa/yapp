package com.memoritta.server.manager;

import com.memoritta.server.client.UserRepository;
import com.memoritta.server.dao.CredentialsDao;
import com.memoritta.server.dao.UserDao;
import com.memoritta.server.mapper.UserMapper;
import com.memoritta.server.model.Credentials;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAccessManagerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordUtils passwordUtils;

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
        String login = "test@example.com";
        String rawPassword = "secret";
        String encryptedPassword = "encrypted123";

        UserDao userDao = new UserDao();
        CredentialsDao credentialsDao = new CredentialsDao();
        credentialsDao.setEmail(login);
        credentialsDao.setEncryptedPassword(encryptedPassword);
        userDao.setCredentials(credentialsDao);
        userDao.setNickname(nickname);

        User expectedUser = new User();
        expectedUser.setId(UUID.randomUUID());
        expectedUser.setNickname("nickname");
        Credentials credentials = new Credentials();
        credentials.setEmail("email");
        credentials.setEncryptedPassword("encryptedpassword");
        expectedUser.setCredentials(credentials);

        when(passwordUtils.encrypt(rawPassword)).thenReturn(encryptedPassword);
        when(userRepository.findByCredentials(login, encryptedPassword)).thenReturn(Optional.of(userDao));
        when(userMapper.toUser(userDao)).thenReturn(expectedUser);

        // When
        User result = userAccessManager.validateCredentials(login, rawPassword);

        // Then
        assertNotNull(result);
        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getNickname(), result.getNickname());

        verify(passwordUtils).encrypt(rawPassword);
        verify(userRepository).findByCredentials(login, encryptedPassword);
        verify(userMapper).toUser(userDao);
    }

    @Test
    void testSaveUser_shouldSaveUserDaoToRepository() {
        // Given
        String login = "save@example.com";
        String encryptedPassword = "encPwd123";
        String nickname = "testNick";

        ArgumentCaptor<UserDao> captor = ArgumentCaptor.forClass(UserDao.class);

        // When
        userAccessManager.saveUser(login, encryptedPassword, nickname);

        // Then
        verify(userRepository).save(captor.capture());
        UserDao savedUserDao = captor.getValue();

        assertNotNull(savedUserDao);
        assertNotNull(savedUserDao.getUser());
        assertEquals(nickname, savedUserDao.getUser().getNickname());

        assertNotNull(savedUserDao.getCredentials());
        assertEquals(login, savedUserDao.getCredentials().getEmail());
        assertEquals(encryptedPassword, savedUserDao.getCredentials().getEncryptedPassword());
    }
}
