package com.memoritta.server.client;

import com.memoritta.server.dao.AnswerDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends MongoRepository<AnswerDao, UUID> {
    List<AnswerDao> findByQuestionId(UUID questionId);
}
