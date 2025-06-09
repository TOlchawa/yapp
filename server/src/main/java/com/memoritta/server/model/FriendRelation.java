package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class FriendRelation {
    private UUID id;
    private UUID userId;
    private UUID friendId;
    private FriendshipType type;
}
