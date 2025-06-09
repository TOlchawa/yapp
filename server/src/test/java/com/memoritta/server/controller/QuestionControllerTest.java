package com.memoritta.server.controller;

import com.memoritta.server.client.QuestionRepository;
import com.memoritta.server.dao.QuestionDao;
import com.memoritta.server.manager.QuestionManager;
import com.memoritta.server.model.QuestionRef;
import com.memoritta.server.model.QuestionAudience;
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
@ContextConfiguration(classes = {QuestionControllerTest.Config.class, QuestionManager.class, QuestionController.class})
class QuestionControllerTest {

    @Configuration
    static class Config {
        @Bean
        QuestionRepository questionRepository() {
            return mock(QuestionRepository.class);
        }
    }

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionController questionController;

    @BeforeEach
    void resetRepo() {
        reset(questionRepository);
    }

    @Test
    void askQuestion_shouldReturnUuid() {
        UUID id = UUID.randomUUID();
        when(questionRepository.save(any(QuestionDao.class)))
                .thenReturn(QuestionDao.builder().id(id).build());

        UUID result = questionController.askQuestion(id.toString(), null, "hi?", "DIRECT");

        assertThat(result).isEqualTo(id);
    }

    @Test
    void listQuestions_shouldReturnFiltered() {
        UUID userId = UUID.randomUUID();
        QuestionDao direct = QuestionDao.builder()
                .id(UUID.randomUUID())
                .toUserId(userId)
                .audience(QuestionAudience.DIRECT)
                .build();
        QuestionDao publicQ = QuestionDao.builder()
                .id(UUID.randomUUID())
                .audience(QuestionAudience.EVERYONE)
                .build();
        when(questionRepository.findAll()).thenReturn(List.of(direct, publicQ));

        List<QuestionRef> result = questionController.listQuestions(userId.toString());

        assertThat(result).hasSize(2);
    }
}
