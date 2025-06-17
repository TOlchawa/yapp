package com.memoritta.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class OpenAiConfig {
    @Value("${openai.url:https://api.openai.com/v1/chat/completions}")
    private String url;

    @Value("${openai.api-key:changeme}")
    private String apiKey;
}
