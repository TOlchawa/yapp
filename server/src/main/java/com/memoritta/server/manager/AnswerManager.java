package com.memoritta.server.manager;

import com.memoritta.server.client.AnswerRepository;
import com.memoritta.server.mapper.AnswerMapper;
import com.memoritta.server.model.Answer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AnswerManager {

    private final AnswerRepository answerRepository;

    public UUID addAnswer(UUID questionId, UUID fromUserId, String text) {
        Answer answer = Answer.builder()
                .id(UUID.randomUUID())
                .questionId(questionId)
                .fromUserId(fromUserId)
                .text(text)
                .build();
        return answerRepository.save(AnswerMapper.INSTANCE.toAnswerDao(answer)).getId();
    }

    public List<Answer> listAnswers(UUID questionId) {
        return answerRepository.findByQuestionId(questionId).stream()
                .map(AnswerMapper.INSTANCE::toAnswer)
                .toList();
    }
}
