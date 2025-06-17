package com.memoritta.server.controller;

import com.memoritta.server.manager.OpenAiManager;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/ai")
public class AiController {

    private final OpenAiManager openAiManager;

    @PostMapping("/smooth")
    @Operation(
            summary = "Smooth text",
            description = "Uses OpenAI to smooth and check spelling of provided text"
    )
    public String smooth(@RequestBody String text) {
        return openAiManager.smoothText(text);
    }
}
