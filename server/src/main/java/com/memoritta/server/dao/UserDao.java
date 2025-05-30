package com.memoritta.server.dao;

import com.memoritta.server.model.Credentials;
import com.memoritta.server.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

@Setter
@Getter
@Builder
public class UserDao {
    @Id
    private UUID id;
    private String nickname;
    @Indexed(unique = true)
    private String email;
    private boolean verified;
    private String encryptedPassword;
}
