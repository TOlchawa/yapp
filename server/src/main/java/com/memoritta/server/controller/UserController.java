package com.memoritta.server.controller;

import com.memoritta.server.manager.UserAccessManager;
import com.memoritta.server.model.AuthenticatedUser;
import com.memoritta.server.model.Credentials;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import com.memoritta.server.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private PasswordUtils passwordUtils;
    private UserUtils userUtils;
    private UserAccessManager userAccessManager;

    @PostMapping
    public AuthenticatedUser login(@RequestBody Credentials credentials) {
        User user = userAccessManager.authenticateAndFetchUser(credentials.getEmail(), credentials.getPassword());
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .user(user)
                .jwtToken(passwordUtils.generateJwtToken(user))
                .build();
        return authenticatedUser;
    }

    @PutMapping
    public UUID createUser(@RequestParam String email, @RequestParam String password, @RequestParam String nickname) {
        String encryptedPassword = passwordUtils.encrypt(password);
        UUID createdUserId = userAccessManager.createUser(email, encryptedPassword, userUtils.getOrRandomNickName(nickname));
        return createdUserId;
    }

}
