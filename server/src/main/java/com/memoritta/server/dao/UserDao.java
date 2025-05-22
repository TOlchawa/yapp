package com.memoritta.server.dao;

import com.memoritta.server.model.Credentials;
import com.memoritta.server.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class UserDao {
    private UUID id;
    private String nickname;
    private CredentialsDao credentials;
}
