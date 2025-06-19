package com.memoritta.server.manager;

import com.memoritta.server.client.AnswerRepository;
import com.memoritta.server.client.QuestionRepository;
import com.memoritta.server.dao.QuestionDao;
import com.memoritta.server.model.Question;
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
import java.time.Instant;
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
        @Bean
        AnswerRepository answerRepository() {
            return mock(AnswerRepository.class);
        }
    }

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionManager questionManager;

    @BeforeEach
    void resetRepo() {
        reset(questionRepository, answerRepository);
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
        String longText = "x".repeat(250);
        QuestionDao q1 = QuestionDao.builder()
                .id(UUID.randomUUID())
                .toUserId(UUID.randomUUID())
                .audience(QuestionAudience.DIRECT)
                .build();
        QuestionDao q2 = QuestionDao.builder()
                .id(UUID.randomUUID())
                .toUserId(userId)
                .audience(QuestionAudience.DIRECT)
                .question("short q2?")
                .createdAt(Instant.now())
                .build();
        QuestionDao q3 = QuestionDao.builder()
                .id(UUID.randomUUID())
                .audience(QuestionAudience.EVERYONE)
                .question(longText)
                .createdAt(Instant.now())
                .build();
        when(questionRepository.findAll()).thenReturn(List.of(q1, q2, q3));
        when(answerRepository.findByQuestionId(any(UUID.class))).thenReturn(List.of());

        List<QuestionRef> result = questionManager.listQuestionsForUser(userId);

        assertThat(result).extracting(QuestionRef::getId)
                .containsExactlyInAnyOrder(q2.getId(), q3.getId());

        QuestionRef refForQ3 = result.stream()
                .filter(r -> r.getId().equals(q3.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(refForQ3.getDescription()).hasSize(200);
    }

    @Test
    void listQuestionIdsForUser_shouldFilterDirectAudience() {
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

        List<UUID> result = questionManager.listQuestionIdsForUser(userId);

        assertThat(result).containsExactlyInAnyOrder(q2.getId(), q3.getId());
    }

    @Test
    void listAllQuestionIds_shouldReturnIds() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(questionRepository.findAll()).thenReturn(List.of(
                QuestionDao.builder().id(id1).build(),
                QuestionDao.builder().id(id2).build()
        ));

        List<UUID> result = questionManager.listAllQuestionIds();

        assertThat(result).containsExactly(id1, id2);
    }

    @Test
    void updateQuestion_shouldSaveChangedText() {
        UUID id = UUID.randomUUID();
        QuestionDao dao = QuestionDao.builder().id(id).question("old").build();
        when(questionRepository.findById(id)).thenReturn(java.util.Optional.of(dao));

        questionManager.updateQuestion(id, "new text");

        verify(questionRepository).save(argThat(q -> "new text".equals(q.getQuestion())));
    }

}
