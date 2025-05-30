package com.memoritta.server.manager;

import com.memoritta.server.client.UserRepository;
import com.memoritta.server.dao.UserDao;
import com.memoritta.server.mapper.UserMapper;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserAccessManager {

    private final UserMapper userMapper;
    private final PasswordUtils passwordUtils;
    private final UserRepository userRepository;

    @SneakyThrows
    public User authenticateAndFetchUser(String email, String password) {
        Optional<UserDao> userDao = userRepository.findByEmail(email);
        passwordUtils.verifyPassword(userDao.get().getEncryptedPassword(), password);
        User result = userMapper.toUser(userDao.get());
        return result;
    }

    @SneakyThrows
    public UUID createUser(String email, String encryptedPassword, String nickname) {
        UserDao userDao = UserDao.builder()
                .id(UUID.randomUUID())
                .nickname(nickname)
                .email(email)
                .encryptedPassword(encryptedPassword)
                .build();
        Optional<UserDao> result = Optional.of(userRepository.save(userDao));
        return result.get().getId();
    }
}
