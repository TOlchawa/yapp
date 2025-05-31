package com.memoritta.server.client;

import com.memoritta.server.dao.UserDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<UserDao, UUID> {
    Optional<UserDao> findByEmail(String email);
}