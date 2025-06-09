package com.memoritta.server.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import com.memoritta.server.model.QuestionAudience;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@Document(collection = "questions")
public class QuestionDao {
    @Id
    private UUID id;
    private UUID fromUserId;
    private UUID toUserId;
    private String question;
    private QuestionAudience audience;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant modifiedAt;
    @CreatedBy
    private UUID createdBy;
}
