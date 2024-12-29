package com.memoritta.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
public class ServerConfig {
    private String version = "1.0.0";
}
