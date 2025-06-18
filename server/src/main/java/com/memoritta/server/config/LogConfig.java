package com.memoritta.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class LogConfig {
    @Value("${server.log:/home/yapp/server/yapp.log}")
    private String logPath;
}
