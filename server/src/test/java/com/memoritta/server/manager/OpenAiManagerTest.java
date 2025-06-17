package com.memoritta.server.manager;

import com.memoritta.server.config.OpenAiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@ContextConfiguration(classes = {OpenAiManagerTest.Config.class, OpenAiManager.class})
class OpenAiManagerTest {

    @Configuration
    static class Config {
        @Bean
        OpenAiConfig openAiConfig() {
            OpenAiConfig cfg = new OpenAiConfig();
            cfg.setUrl("http://test");
            cfg.setApiKey("key");
            return cfg;
        }
    }

    private MockRestServiceServer server;

    @org.springframework.beans.factory.annotation.Autowired
    private OpenAiManager openAiManager;

    @BeforeEach
    void setUp() {
        RestTemplate template = (RestTemplate) ReflectionTestUtils.getField(openAiManager, "restTemplate");
        server = MockRestServiceServer.bindTo(template).build();
    }

    @Test
    void smoothText_shouldReturnContent() {
        server.expect(requestTo("http://test"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"choices\":[{\"message\":{\"content\":\"done\"}}]}", MediaType.APPLICATION_JSON));

        String result = openAiManager.smoothText("text");

        assertThat(result).isEqualTo("done");
        server.verify();
    }

    @Test
    void smoothText_emptyChoices_shouldReturnOriginal() {
        server.expect(requestTo("http://test"))
                .andRespond(withSuccess("{\"choices\":[]}", MediaType.APPLICATION_JSON));

        String input = "orig";
        String result = openAiManager.smoothText(input);

        assertThat(result).isEqualTo(input);
        server.verify();
    }

    @Test
    void smoothText_nullResponse_shouldReturnOriginal() {
        server.expect(requestTo("http://test"))
                .andRespond(withNoContent());

        String input = "orig";
        String result = openAiManager.smoothText(input);

        assertThat(result).isEqualTo(input);
        server.verify();
    }
}
