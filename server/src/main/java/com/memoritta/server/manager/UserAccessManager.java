package com.memoritta.server.manager;

import com.memoritta.server.client.UserRepository;
import com.memoritta.server.dao.CredentialsDao;
import com.memoritta.server.dao.UserDao;
import com.memoritta.server.mapper.UserMapper;
import com.memoritta.server.model.Credentials;
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
    public User validateCredentials(String login, String password) {
        String encryptedPassword = passwordUtils.encrypt(password);
        Optional<UserDao> userDao = userRepository.findByCredentials(login, encryptedPassword);
        User result = userMapper.toUser(userDao.get());
        return result;
    }

    @SneakyThrows
    public UUID saveUser(String login, String encryptedPassword, String nikcname) {
        UserDao userDao = new UserDao();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setNickname(nikcname);;
        CredentialsDao credentialsDao = new CredentialsDao();
        credentialsDao.setEmail(login);
        credentialsDao.setEncryptedPassword(encryptedPassword);
        userDao.setCredentials(credentialsDao);
        Optional<UserDao> result = userRepository.save(userDao);
        return result.get().getId();
    }
}
