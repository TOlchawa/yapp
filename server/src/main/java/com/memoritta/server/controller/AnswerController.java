package com.memoritta.server.controller;

import com.memoritta.server.manager.AnswerManager;
import com.memoritta.server.model.Answer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/answer")
public class AnswerController {

    private final AnswerManager answerManager;

    @PostMapping
    @Operation(summary = "Add answer", description = "Adds an answer to a question")
    public UUID addAnswer(
            @RequestParam @Parameter(description = "Question ID") String questionId,
            @RequestParam @Parameter(description = "User answering") String fromUserId,
            @RequestParam @Parameter(description = "Answer text") String text
    ) {
        return answerManager.addAnswer(
                UUID.fromString(questionId),
                UUID.fromString(fromUserId),
                text
        );
    }

    @GetMapping
    @Operation(summary = "List answers", description = "Lists answers for a question")
    public List<Answer> listAnswers(
            @RequestParam @Parameter(description = "Question ID") String questionId
    ) {
        return answerManager.listAnswers(UUID.fromString(questionId));
    }
}
