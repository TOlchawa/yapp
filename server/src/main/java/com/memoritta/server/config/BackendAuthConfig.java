package com.memoritta.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class BackendAuthConfig {
    @Value("${backend.user:admin}")
    private String user;

    @Value("${backend.password:admin}")
    private String password;
}
