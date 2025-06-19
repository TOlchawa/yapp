package com.memoritta.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class OpenAiConfig {
    // Base URL for OpenAI services. Do not include the path to a specific endpoint.
    @Value("${openai.url:https://api.openai.com/v1}")
    private String url;

    @Value("${openai.api-key:changeme}")
    private String apiKey;

    @Value("${openai.organization:}")
    private String organization;

    @Value("${openai.project:}")
    private String project;
}
