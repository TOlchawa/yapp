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
public class Answer {
    private UUID id;
    private UUID questionId;
    private UUID fromUserId;
    private String text;
}
