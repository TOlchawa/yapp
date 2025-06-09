package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class Answer {
    private UUID id;
    private UUID questionId;
    private UUID fromUserId;
    private String text;
}
