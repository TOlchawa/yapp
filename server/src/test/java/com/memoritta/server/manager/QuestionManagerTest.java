package com.memoritta.server.manager;

import com.memoritta.server.client.QuestionRepository;
import com.memoritta.server.dao.QuestionDao;
import com.memoritta.server.model.Question;
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
@ContextConfiguration(classes = {QuestionManagerTest.Config.class, QuestionManager.class})
class QuestionManagerTest {

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
    private QuestionManager questionManager;

    @BeforeEach
    void resetRepo() {
        reset(questionRepository);
    }

    @Test
    void askQuestion_shouldSaveAndReturnId() {
        UUID id = UUID.randomUUID();
        when(questionRepository.save(any(QuestionDao.class)))
                .thenReturn(QuestionDao.builder().id(id).build());

        UUID result = questionManager.askQuestion(id, null, "test?", QuestionAudience.DIRECT);

        assertThat(result).isEqualTo(id);
        verify(questionRepository).save(any(QuestionDao.class));
    }

    @Test
    void listQuestionsForUser_shouldFilterDirectAudience() {
        UUID userId = UUID.randomUUID();
        QuestionDao q1 = QuestionDao.builder()
                .id(UUID.randomUUID())
                .toUserId(UUID.randomUUID())
                .audience(QuestionAudience.DIRECT)
                .build();
        QuestionDao q2 = QuestionDao.builder()
                .id(UUID.randomUUID())
                .toUserId(userId)
                .audience(QuestionAudience.DIRECT)
                .build();
        QuestionDao q3 = QuestionDao.builder()
                .id(UUID.randomUUID())
                .audience(QuestionAudience.EVERYONE)
                .build();
        when(questionRepository.findAll()).thenReturn(List.of(q1, q2, q3));

        List<Question> result = questionManager.listQuestionsForUser(userId);

        assertThat(result).extracting(Question::getId)
                .containsExactlyInAnyOrder(q2.getId(), q3.getId());
    }
}
