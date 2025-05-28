package com.memoritta.server.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServerControllerTest {

    @InjectMocks
    private ServerController serverController;


    @Test
    void testPingEndpointReturnsUuid() {
        // When
        UUID result = serverController.registerItemWithImage("ping");

        // Then
        assertNotNull(result);
    }
}
