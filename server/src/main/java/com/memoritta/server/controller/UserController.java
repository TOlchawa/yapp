package com.memoritta.server.controller;

import com.memoritta.server.manager.UserAccessManager;
import com.memoritta.server.model.AuthenticatedUser;
import com.memoritta.server.model.Credentials;
import com.memoritta.server.model.User;
import com.memoritta.server.utils.PasswordUtils;
import com.memoritta.server.utils.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(
            summary = "Authenticate user and return JWT token",
            description = "Authenticates the user with the given credentials and returns a JWT token along with user details."
    )
    public AuthenticatedUser login(
            @RequestBody
            @Parameter(description = "User credentials: email and password")
            Credentials credentials
    ) {
        User user = userAccessManager.authenticateAndFetchUser(credentials.getEmail(), credentials.getPassword());
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .user(user)
                .jwtToken(passwordUtils.generateJwtToken(user))
                .build();
        return authenticatedUser;
    }

    @PutMapping
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided email, password, and nickname. " +
                    "The password is encrypted before storage. If the nickname is blank or null, a random one is generated."
    )
    public UUID createUser(
            @RequestParam
            @Parameter(description = "Email address for the new user", required = true, example = "user@example.com")
            String email,

            @RequestParam
            @Parameter(description = "Raw password to be encrypted", required = true, example = "mySecretPassword")
            String password,

            @RequestParam
            @Parameter(description = "Optional nickname; if not provided, a random one will be generated", example = "coolUser42")
            String nickname
    ) {
        String encryptedPassword = passwordUtils.encrypt(password);
        UUID createdUserId = userAccessManager.createUser(email, encryptedPassword, userUtils.getOrRandomNickName(nickname));
        return createdUserId;
    }
}