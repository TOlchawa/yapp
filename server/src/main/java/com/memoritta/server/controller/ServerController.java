package com.memoritta.server.controller;

import com.memoritta.server.config.ServerConfig;
import com.memoritta.server.config.LogConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final ServerConfig serverConfig;
    private final LogConfig logConfig;


    @GetMapping("/version")
    @Operation(
            summary = "Get application version",
            description = "Returns the current version string of the running server instance. Useful for health checks and diagnostics."
    )
    public String getVersion() {
        log.debug("getVersion");
        return serverConfig.getVersion();
    }

    /**
     * Registers a ping.
     *
     * @param ping arbitrary string used to register a ping event
     * @return a randomly generated UUID confirming the ping registration
     */
    @SneakyThrows
    @PostMapping("/ping")
    @Operation(
            summary = "Register a ping event",
            description = "Accepts a ping value and returns a randomly generated UUID to acknowledge the request."
    )
    public UUID pingPost(
            @RequestParam
            @Parameter(description = "Ping value to register (any arbitrary string)", example = "heartbeat-123")
            String ping
    ) {
        return UUID.randomUUID();
    }

    @GetMapping("/ping")
    @Operation(
            summary = "Ping check",
            description = "Simple ping endpoint that returns a 'pong' string. Useful for checking service availability."
    )
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @SneakyThrows
    @GetMapping("/logs")
    @Operation(
            summary = "Read server log",
            description = "Returns the content of the configured log file"
    )
    public ResponseEntity<String> getLogs() {
        java.nio.file.Path path = java.nio.file.Path.of(logConfig.getLogPath());
        if (!java.nio.file.Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        String text = java.nio.file.Files.readString(path);
        return ResponseEntity.ok(text);
    }
}
