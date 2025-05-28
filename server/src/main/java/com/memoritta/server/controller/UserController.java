package com.memoritta.server.controller;

import com.memoritta.server.manager.UserAccessManager;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import com.memoritta.server.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private PasswordUtils passwordUtils;
    private UserUtils userUtils;
    private UserAccessManager userAccessManager;

    @GetMapping("/user")
    public User getUser(String login, String password) {
        String encryptedPassword = passwordUtils.encrypt(password);
        User user = userAccessManager.validateCredentials(login, encryptedPassword);
        return user;
    }

    @PutMapping("/user")
    public User putUser(String email, String password, String nickname) {
        String encryptedPassword = passwordUtils.encrypt(password);
        userAccessManager.createUser(email, password, userUtils.getOrRandomNickName(nickname));
        User user = userAccessManager.validateCredentials(email, encryptedPassword);
        return user;
    }
}
