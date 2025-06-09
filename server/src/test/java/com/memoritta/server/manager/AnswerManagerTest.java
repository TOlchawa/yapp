package com.memoritta.server.manager;

import com.memoritta.server.client.AnswerRepository;
import com.memoritta.server.dao.AnswerDao;
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
@ContextConfiguration(classes = {AnswerManagerTest.Config.class, AnswerManager.class})
class AnswerManagerTest {

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
    private AnswerManager answerManager;

    @BeforeEach
    void resetRepo() {
        reset(answerRepository);
    }

    @Test
    void addAnswer_shouldReturnId() {
        UUID id = UUID.randomUUID();
        when(answerRepository.save(any(AnswerDao.class)))
                .thenReturn(AnswerDao.builder().id(id).build());

        UUID result = answerManager.addAnswer(id, id, "text");

        assertThat(result).isEqualTo(id);
        verify(answerRepository).save(any(AnswerDao.class));
    }

    @Test
    void listAnswers_shouldReturnMapped() {
        UUID qid = UUID.randomUUID();
        AnswerDao dao = AnswerDao.builder()
                .id(UUID.randomUUID())
                .questionId(qid)
                .fromUserId(UUID.randomUUID())
                .text("a")
                .build();
        when(answerRepository.findByQuestionId(qid)).thenReturn(List.of(dao));

        List<Answer> result = answerManager.listAnswers(qid);

        assertThat(result).hasSize(1);
    }
}
