package com.memoritta.server.controller;

import com.memoritta.server.manager.QuestionManager;
import com.memoritta.server.model.Question;
import com.memoritta.server.model.QuestionRef;
import com.memoritta.server.model.QuestionAudience;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/question")
public class QuestionController {

    private final QuestionManager questionManager;

    @PostMapping
    @Operation(summary = "Ask a question", description = "Creates a new question")
    public UUID askQuestion(
            @RequestParam @Parameter(description = "ID of user asking") String fromUserId,
            @RequestParam(required = false) @Parameter(description = "ID of user to ask") String toUserId,
            @RequestParam @Parameter(description = "Question text") String question,
            @RequestParam(defaultValue = "DIRECT") @Parameter(description = "Question audience: EVERYONE, FRIENDS, BEST_FRIENDS, DIRECT") String audience
    ) {
        UUID toId = toUserId == null ? null : UUID.fromString(toUserId);
        QuestionAudience aud = QuestionAudience.valueOf(audience);
        return questionManager.askQuestion(UUID.fromString(fromUserId), toId, question, aud);
    }

    @GetMapping
    @Operation(summary = "List questions", description = "Lists questions for the given user")
    public List<QuestionRef> listQuestions(
            @RequestParam @Parameter(description = "ID of user to get questions for") String userId
    ) {
        return questionManager.listQuestionsForUser(UUID.fromString(userId));
    }

    @GetMapping("/ids")
    @Operation(summary = "List question IDs", description = "Lists question IDs for the given user")
    public List<UUID> listQuestionIds(
            @RequestParam @Parameter(description = "ID of user to get questions for") String userId
    ) {
        return questionManager.listQuestionIdsForUser(UUID.fromString(userId));
    }

    @GetMapping("/ids/all")
    @Operation(summary = "List all question IDs", description = "Lists IDs of all questions in the database")
    public List<UUID> listAllQuestionIds() {
        return questionManager.listAllQuestionIds();
    }

    @GetMapping("/detail")
    @Operation(summary = "Get question", description = "Fetch single question with answers")
    public Question fetchQuestion(
            @RequestParam @Parameter(description = "Question ID") String id
    ) {
        return questionManager.fetchQuestion(UUID.fromString(id));
    }

    @PutMapping
    @Operation(summary = "Edit question", description = "Updates question text")
    public void editQuestion(
            @RequestParam @Parameter(description = "Question ID") String id,
            @RequestParam @Parameter(description = "New question text") String question
    ) {
        questionManager.updateQuestion(UUID.fromString(id), question);
    }
}
