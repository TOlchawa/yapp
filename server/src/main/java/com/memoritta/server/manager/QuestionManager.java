package com.memoritta.server.manager;

import com.memoritta.server.client.QuestionRepository;
import com.memoritta.server.mapper.QuestionMapper;
import com.memoritta.server.model.Question;
import com.memoritta.server.model.QuestionAudience;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuestionManager {

    private final QuestionRepository questionRepository;

    public UUID askQuestion(UUID fromUserId, UUID toUserId, String questionText, QuestionAudience audience) {
        Question question = Question.builder()
                .id(UUID.randomUUID())
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .question(questionText)
                .audience(audience)
                .build();
        Question saved = QuestionMapper.INSTANCE.toQuestion(questionRepository.save(QuestionMapper.INSTANCE.toQuestionDao(question)));
        return saved.getId();
    }

    public List<Question> listQuestionsForUser(UUID userId) {
        return questionRepository.findAll().stream()
                .filter(dao -> dao.getAudience() != QuestionAudience.DIRECT || userId.equals(dao.getToUserId()))
                .map(QuestionMapper.INSTANCE::toQuestion)
                .toList();
    }
}
