package com.memoritta.server.manager;

import com.memoritta.server.config.OpenAiConfig;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class OpenAiManager {

    private final RestTemplate restTemplate = new RestTemplate();
    private final OpenAiConfig config;

    @Retryable(
            value = { HttpClientErrorException.TooManyRequests.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String smoothText(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getApiKey());
        if (config.getOrganization() != null && !config.getOrganization().isBlank()) {
            headers.add("OpenAI-Organization", config.getOrganization());
        }
        if (config.getProject() != null && !config.getProject().isBlank()) {
            headers.add("OpenAI-Project", config.getProject());
        }

        Map<String, Object> messageDeveloper = Map.of(
                "role", "developer",
                "content", List.of(
                        Map.of(
                                "type", "text",
                                "text", "Keep the original language. Please smooth and check spelling of this text. Keep the original language.\\n"
                        )
                )
        );
        Map<String, Object> messageUser = Map.of(
                "role", "user",
                "content", List.of(
                        Map.of(
                                "type", "text",
                                "text", "\n" + text
                        )
                )
        );
        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(messageDeveloper, messageUser)
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
