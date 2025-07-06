package com.memoritta.server.controller;

import com.memoritta.server.manager.FriendRelationManager;
import com.memoritta.server.model.FriendRelation;
import com.memoritta.server.model.FriendshipType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/friend")
public class FriendRelationController {

    private final FriendRelationManager manager;

    @PostMapping
    @Operation(summary = "Add friend relation", description = "Adds a friend or best friend relation")
    public UUID addFriend(
            @RequestParam @Parameter(description = "User ID owning relation") String userId,
            @RequestParam @Parameter(description = "Friend ID") String friendId,
            @RequestParam(defaultValue = "FRIENDS") @Parameter(description = "Relation type: FRIENDS or BEST_FRIENDS") String type
    ) {
        return manager.addFriend(
                UUID.fromString(userId),
                UUID.fromString(friendId),
                FriendshipType.valueOf(type)
        );
    }

    @GetMapping
    @Operation(summary = "List friends", description = "Lists relations for user")
    public List<FriendRelation> listFriends(
            @RequestParam @Parameter(description = "User ID to list relations for") String userId
    ) {
        return manager.listFriends(UUID.fromString(userId));
    }

    @DeleteMapping
    @Operation(summary = "Remove friend relation", description = "Deletes a friend relation")
    public void removeFriend(
            @RequestParam @Parameter(description = "User ID owning relation") String userId,
            @RequestParam @Parameter(description = "Friend ID") String friendId
    ) {
        manager.removeFriend(UUID.fromString(userId), UUID.fromString(friendId));
    }

    @PatchMapping
    @Operation(summary = "Change friend type", description = "Updates relation type")
    public FriendRelation changeFriendType(
            @RequestParam @Parameter(description = "User ID owning relation") String userId,
            @RequestParam @Parameter(description = "Friend ID") String friendId,
            @RequestParam(defaultValue = "FRIENDS") @Parameter(description = "Relation type: FRIENDS or BEST_FRIENDS") String type
    ) {
        return manager.changeFriendType(
                UUID.fromString(userId),
                UUID.fromString(friendId),
                FriendshipType.valueOf(type)
        );
    }
}
