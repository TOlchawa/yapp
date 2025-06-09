package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.List;

@Getter
@Setter
@Builder
public class Question {
    private UUID id;
    private UUID fromUserId;
    private UUID toUserId;
    private String question;
    private List<Answer> answers;
    private QuestionAudience audience;
}
