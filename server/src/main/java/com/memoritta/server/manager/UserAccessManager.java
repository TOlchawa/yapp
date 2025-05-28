package com.memoritta.server.manager;

import com.memoritta.server.client.UserRepository;
import com.memoritta.server.dao.UserDao;
import com.memoritta.server.mapper.UserMapper;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserAccessManager {

    private UserMapper userMapper;
    private PasswordUtils passwordUtils;
    private UserRepository userRepository;

    @SneakyThrows
    public User validateCredentials(String email, String password) {
        Optional<UserDao> userDao = userRepository.findByEmail(email);
        passwordUtils.verifyPassword(userDao.get().getEncryptedPassword(), password);
        User result = userMapper.toUser(userDao.get());
        return result;
    }

    @SneakyThrows
    public UUID createUser(String email, String encryptedPassword, String nikcname) {
        UserDao userDao = new UserDao();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setNickname(nikcname);;
        userDao.setEmail(email);
        userDao.setEncryptedPassword(encryptedPassword);
        Optional<UserDao> result = Optional.of(userRepository.save(userDao));
        return result.get().getId();
    }
}
