package com.memoritta.server.controller;

import com.memoritta.server.config.ServerConfig;
import com.memoritta.server.manager.UserAccessManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class ServerController {
    private ServerConfig serverConfig;

    @GetMapping("/version")
    public String getVersion() {
        return serverConfig.getVersion();
    }
}
