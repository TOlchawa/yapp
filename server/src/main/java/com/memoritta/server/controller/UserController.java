package com.memoritta.server.controller;

import com.memoritta.server.manager.UserAccessManager;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import com.memoritta.server.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class UserController {

    private PasswordUtils passwordUtils;
    private UserUtils userUtils;
    private UserAccessManager userAccessManager;

    @GetMapping("/user")
    public User fetchUser(String login, String password) {
        User user = userAccessManager.authenticateAndFetchUser(login, password);
        return user;
    }

    @PutMapping("/user")
    public UUID createUser(String email, String password, String nickname) {
        String encryptedPassword = passwordUtils.encrypt(password);
        UUID createdUserId = userAccessManager.createUser(email, encryptedPassword, userUtils.getOrRandomNickName(nickname));
        return createdUserId;
    }
}
