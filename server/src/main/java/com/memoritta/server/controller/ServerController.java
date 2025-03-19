package com.memoritta.server.controller;

import com.memoritta.server.config.ServerConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
