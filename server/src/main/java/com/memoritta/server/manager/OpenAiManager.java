package com.memoritta.server.manager;

import com.memoritta.server.config.OpenAiConfig;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiManager {

    private final OpenAiConfig config;
    private final RestTemplate restTemplate;

    public OpenAiManager(OpenAiConfig config) {
        this.config = config;
        this.restTemplate = new RestTemplate();
    }

    public String smoothText(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getApiKey());

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", "Please smooth and check spelling of this text. Keep the original language. Text: " + text
        );
        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(message)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        Map<?, ?> response = restTemplate.postForObject(config.getUrl(), entity, Map.class);
        if (response == null) {
            return text;
        }
        List<?> choices = (List<?>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            return text;
        }
        Object choice = choices.get(0);
        if (choice instanceof Map<?,?> choiceMap) {
            Object msg = choiceMap.get("message");
            if (msg instanceof Map<?,?> msgMap) {
                Object content = msgMap.get("content");
                if (content != null) {
                    return content.toString().trim();
                }
            }
        }
        return text;
    }
}
