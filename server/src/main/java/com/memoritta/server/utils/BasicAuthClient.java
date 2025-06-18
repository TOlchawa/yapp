package com.memoritta.server.utils;

import com.memoritta.server.config.BackendAuthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class BasicAuthClient {

    private final RestTemplate restTemplate;

    public BasicAuthClient(BackendAuthConfig config, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .basicAuthentication(config.getUser(), config.getPassword())
                .build();
    }

    public <T> T get(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }

    public <T> T post(String url, Object body, Class<T> responseType) {
        return restTemplate.postForObject(url, body, responseType);
    }
}
