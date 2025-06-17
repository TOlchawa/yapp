package com.memoritta.server.manager;

import com.memoritta.server.client.AnswerRepository;
import com.memoritta.server.client.QuestionRepository;
import com.memoritta.server.mapper.AnswerMapper;
import com.memoritta.server.mapper.QuestionMapper;
import com.memoritta.server.model.Answer;
import com.memoritta.server.model.Question;
import com.memoritta.server.model.QuestionRef;
import com.memoritta.server.model.QuestionAudience;
import com.memoritta.server.dao.QuestionDao;
import org.springframework.beans.factory.annotation.Value;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionManager {

    @Value("${question.ref.description-max-length:200}")
    private int descriptionMaxLength;

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionManager(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

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

    public List<QuestionRef> listQuestionsForUser(UUID userId) {
        return questionRepository.findAll().stream()
                .filter(dao -> dao.getAudience() != QuestionAudience.DIRECT || userId.equals(dao.getToUserId()))
                .map(this::toQuestionRef)
                .toList();
    }

    private QuestionRef toQuestionRef(QuestionDao dao) {
        String desc = dao.getQuestion();
        if (desc != null && desc.length() > descriptionMaxLength) {
            desc = desc.substring(0, descriptionMaxLength);
        }
        int answerCount = answerRepository.findByQuestionId(dao.getId()).size();
        return QuestionRef.builder()
                .id(dao.getId())
                .createdAt(dao.getCreatedAt())
                .description(desc)
                .answerCount(answerCount)
                .build(); 
    }

    public Question fetchQuestion(UUID uuid) {
        Question result = QuestionMapper.INSTANCE.toQuestion(questionRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + uuid)));
        return result;
    }

    public void updateQuestion(UUID id, String questionText) {
        QuestionDao dao = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));
        if (questionText != null) {
            dao.setQuestion(questionText);
        }
        questionRepository.save(dao);
    }
}
