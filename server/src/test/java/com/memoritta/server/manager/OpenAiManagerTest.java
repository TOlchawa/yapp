package com.memoritta.server.manager;

import com.memoritta.server.config.OpenAiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.assertj.core.api.Assertions.assertThat;

class OpenAiManagerTest {
    private RestTemplate restTemplate;
    private OpenAiManager manager;
    private MockRestServiceServer server;

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        OpenAiConfig config = new OpenAiConfig();
        config.setUrl("http://test");
        config.setApiKey("secret");
        manager = new OpenAiManager(config);
        org.springframework.test.util.ReflectionTestUtils.setField(manager, "restTemplate", restTemplate);
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void smoothText_shouldReturnContent() {
        String body = "{\"choices\":[{\"message\":{\"content\":\"done\"}}]}";
        server.expect(once(), requestTo("http://test"))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        String result = manager.smoothText("hello");

        assertThat(result).isEqualTo("done");
    }

    @Test
    void smoothText_nullResponse_shouldReturnOriginal() {
        server.expect(once(), requestTo("http://test"))
                .andRespond(withSuccess("null", MediaType.APPLICATION_JSON));

        String result = manager.smoothText("hello");

        assertThat(result).isEqualTo("hello");
    }

    @Test
    void smoothText_emptyChoices_shouldReturnOriginal() {
        server.expect(once(), requestTo("http://test"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        String result = manager.smoothText("hello");

        assertThat(result).isEqualTo("hello");
    }
}
