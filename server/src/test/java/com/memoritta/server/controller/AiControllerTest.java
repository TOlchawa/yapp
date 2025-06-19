package com.memoritta.server.controller;

import com.memoritta.server.manager.OpenAiManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {AiControllerTest.Config.class, AiController.class})
class AiControllerTest {

    @Configuration
    static class Config {
        @Bean
        OpenAiManager openAiManager() {
            return mock(OpenAiManager.class);
        }
    }

    @Autowired
    private OpenAiManager openAiManager;

    @Autowired
    private AiController aiController;

    @BeforeEach
    void resetMock() {
        reset(openAiManager);
    }

    @Test
    void smooth_shouldReturnResponse() {
        when(openAiManager.smoothText(anyString())).thenReturn("done");

        String result = aiController.smooth("test");

        assertThat(result).isEqualTo("done");
    }

    @Test
    void transcribe_shouldReturnText() throws Exception {
        when(openAiManager.transcribeAudio(any())).thenReturn("hello");
        MockMultipartFile file = new MockMultipartFile("file", new byte[] {1, 2});

        String result = aiController.transcribe(file);

        assertThat(result).isEqualTo("hello");
    }
}
