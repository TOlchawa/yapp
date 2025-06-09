package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class QuestionRef {
    private UUID id;
    private Instant createdAt;
    private String description;
    private int answerCount;
}
