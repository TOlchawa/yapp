package com.memoritta.server.controller;

import com.memoritta.server.manager.UserAccessManager;
import com.memoritta.server.utils.PasswordUtils;
import com.memoritta.server.utils.UserUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import({ PasswordUtils.class, UserUtils.class, UserController.class, UserAccessManager.class })
class ServerControllerTest {

    @Autowired
    private ServerController serverController;


    @Test
    void testPingEndpointReturnsUuid() {
        // When
        UUID result = serverController.registerItemWithImage("ping");

        // Then
        assertNotNull(result);
    }
}
