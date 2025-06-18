package com.memoritta.server.manager;

import com.memoritta.server.config.OpenAiConfig;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import org.springframework.stereotype.Service;

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
