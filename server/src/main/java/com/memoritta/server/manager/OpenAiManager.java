package com.memoritta.server.manager;

import com.memoritta.server.config.OpenAiConfig;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class OpenAiManager {

    private final OpenAIClient client;
    private static final String DEFAULT_KEY = "changeme";

    public OpenAiManager(OpenAiConfig config) {
        if (config.getApiKey() == null || config.getApiKey().isBlank() || DEFAULT_KEY.equals(config.getApiKey())) {
            System.out.println("OpenAI disabled: API key not provided");
            this.client = null;
        } else {
            this.client = OpenAIOkHttpClient.builder()
                    .apiKey(config.getApiKey())
                    .baseUrl(config.getUrl())
                    .organization(config.getOrganization())
                    .project(config.getProject())
                    .build();
        }
    }

    @Retryable(
            value = {HttpClientErrorException.TooManyRequests.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
     public String smoothText(String text) {
        if (client == null) {
            return text;
        }

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("Keep the original language. Please smooth and improve the following text while keeping it in the original language. Correct grammar, punctuation, and style, but do not translate or change the language:\\n\\n" + text)
                .model(ChatModel.GPT_4O_MINI)
                .build();
        ChatCompletion completion = client.chat().completions().create(params);
        if (completion.choices().isEmpty()) {
            return text;
        }
        return completion.choices().get(0).message().content().orElse(text).trim();
    }
}
