package com.memoritta.server.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Setter
@Getter
@Builder
@Document(collection = "items")
public class UserDao {
    @Id
    private UUID id;
    private String nickname;
    @Indexed(unique = true)
    private String email;
    private boolean verified;
    private String encryptedPassword;
}
