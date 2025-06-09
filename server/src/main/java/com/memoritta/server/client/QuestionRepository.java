package com.memoritta.server.client;

import com.memoritta.server.dao.QuestionDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends MongoRepository<QuestionDao, UUID> {
    List<QuestionDao> findByToUserId(UUID toUserId);
}
