package com.memoritta.server.client;

import com.memoritta.server.dao.FriendRelationDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FriendRelationRepository extends MongoRepository<FriendRelationDao, UUID> {
    List<FriendRelationDao> findByUserId(UUID userId);
}
