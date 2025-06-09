package com.memoritta.server.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Builder
@Document(collection = "users")
@NoArgsConstructor
public class UserDao {
    @Id
    private UUID id;
    @Indexed(unique = true)
    private String email;
    private String nickname;
    private boolean verified;
    private String encryptedPassword;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant modifiedAt;
    @CreatedBy
    private UUID createdBy;

}
