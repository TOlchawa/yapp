package com.memoritta.server.client;

import com.memoritta.server.dao.UserDao;
import com.memoritta.server.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<UserDao, UUID> {
    Optional<UserDao> findById(UUID id);
    Optional<UserDao> findByCredentials(String login, String encryptedPassword);
    Optional<UserDao> save(User user);
}