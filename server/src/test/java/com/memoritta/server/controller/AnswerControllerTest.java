package com.memoritta.server.controller;

import com.memoritta.server.client.AnswerRepository;
import com.memoritta.server.dao.AnswerDao;
import com.memoritta.server.manager.AnswerManager;
import com.memoritta.server.model.Answer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {AnswerControllerTest.Config.class, AnswerManager.class, AnswerController.class})
class AnswerControllerTest {

    @Configuration
    static class Config {
        @Bean
        AnswerRepository answerRepository() {
            return mock(AnswerRepository.class);
        }
    }

    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AnswerController answerController;

    @BeforeEach
    void resetRepo() {
        reset(answerRepository);
    }

    @Test
    void addAnswer_shouldReturnId() {
        UUID id = UUID.randomUUID();
        when(answerRepository.save(any(AnswerDao.class)))
                .thenReturn(AnswerDao.builder().id(id).build());

        UUID result = answerController.addAnswer(id.toString(), id.toString(), "txt");

        assertThat(result).isEqualTo(id);
    }

    @Test
    void listAnswers_shouldReturnData() {
        UUID qid = UUID.randomUUID();
        when(answerRepository.findByQuestionId(qid))
                .thenReturn(List.of(AnswerDao.builder().id(UUID.randomUUID()).questionId(qid).build()));

        List<Answer> result = answerController.listAnswers(qid.toString());

        assertThat(result).hasSize(1);
    }
}
