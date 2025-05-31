package com.memoritta.server.controller;

import com.memoritta.server.config.ServerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ContextConfiguration(classes = {ServerController.class, ServerConfig.class})
class ServerControllerTest {

    @Autowired
    private ServerController serverController;


    @Test
    void testPingEndpointReturnsUuid() {
        // When
        UUID result = serverController.pingPost("ping");

        // Then
        assertNotNull(result);
    }
}
