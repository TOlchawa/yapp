package com.memoritta.server.manager;

import com.memoritta.server.config.OpenAiConfig;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

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
public class OpenAiManager {

    private final OpenAiConfig config;
    private final OpenAIClient client;

    public OpenAiManager(OpenAiConfig config) {
        this.config = config;
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(config.getApiKey())
                .baseUrl(config.getUrl())
                .organization(config.getOrganization())
                .project(config.getProject())
                .build();
    }

    @Retryable(
            value = { HttpClientErrorException.TooManyRequests.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String smoothText(String text) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("Please smooth and improve the following text while keeping it in the original language. Correct grammar, punctuation, and style, but do not translate or change the language: " + text)
                .model(ChatModel.GPT_4_1)
                .build();
        ChatCompletion completion = client.chat().completions().create(params);
        if (completion.choices().isEmpty()) {
            return text;
        }
        return completion.choices().get(0).message().content().orElse(text).trim();
    }
}
