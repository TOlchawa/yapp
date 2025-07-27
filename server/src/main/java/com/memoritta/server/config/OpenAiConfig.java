package com.memoritta.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class OpenAiConfig {
    // Base URL for OpenAI services.
    // Should not contain a concrete endpoint like "/chat/completions".
    // Defaults to "https://api.openai.com/v1".
    @Value("${openai.url:https://api.openai.com/v1}")
    private String url;

    @Value("${openai.api-key:changeme}")
    private String apiKey;

    @Value("${openai.organization:}")
    private String organization;

    @Value("${openai.project:}")
    private String project;

    /**
     * Returns a sanitized base URL for the OpenAI client.
     * <p>
     * Removes trailing "/chat/completions" if present and ensures the URL
     * ends with "/v1".
     */
    public String getNormalizedUrl() {
        String base = url == null ? "" : url.trim();

        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        String suffix = "/chat/completions";
        if (base.endsWith(suffix)) {
            base = base.substring(0, base.length() - suffix.length());
        }

        if (!base.endsWith("/v1")) {
            base = base + "/v1";
        }

        return base;
    }
}
