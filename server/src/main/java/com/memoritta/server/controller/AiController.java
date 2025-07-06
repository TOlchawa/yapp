package com.memoritta.server.controller;

import com.memoritta.server.manager.OpenAiManager;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/transcribe")
    @Operation(
            summary = "Transcribe audio",
            description = "Uses OpenAI Whisper to transcribe provided audio file"
    )
    public String transcribe(@RequestParam("file") MultipartFile file) throws Exception {
        byte[] data = file.getBytes();
        return openAiManager.transcribeAudio(data);
    }

    @PostMapping("/ask")
    @Operation(
            summary = "Answer question",
            description = "Uses OpenAI to answer given question"
    )
    public String ask(@RequestBody String question) {
        return openAiManager.answerQuestion(question);
    }
}
