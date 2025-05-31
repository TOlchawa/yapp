package com.memoritta.server.controller;

import com.memoritta.server.config.ServerConfig;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class ServerController {
    private ServerConfig serverConfig;

    @GetMapping("/version")
    public String getVersion() {
        log.debug("getVersion");
        return serverConfig.getVersion();
    }

    /**
     * Registers a ping.
     *
     * @return the UUID of the registered item
     */
    @SneakyThrows
    @PostMapping("/ping")
    public UUID pingPost(@RequestParam String ping) {
        return UUID.randomUUID();
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

}
