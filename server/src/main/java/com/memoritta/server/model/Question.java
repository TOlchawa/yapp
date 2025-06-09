package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class Question {
    private UUID id;
    private UUID fromUserId;
    private UUID toUserId;
    private String question;
    private String answer;
    private QuestionAudience audience;
}
