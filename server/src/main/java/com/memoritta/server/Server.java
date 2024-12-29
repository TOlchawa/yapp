package com.memoritta.server;

import com.memoritta.server.config.ServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@SpringBootApplication
@ContextConfiguration(classes = {ServerConfig.class})
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
