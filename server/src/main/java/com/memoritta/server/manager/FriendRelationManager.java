package com.memoritta.server.manager;

import com.memoritta.server.client.FriendRelationRepository;
import com.memoritta.server.mapper.FriendRelationMapper;
import com.memoritta.server.model.FriendRelation;
import com.memoritta.server.model.FriendshipType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FriendRelationManager {

    private final FriendRelationRepository repository;

    public UUID addFriend(UUID userId, UUID friendId, FriendshipType type) {
        FriendRelation relation = FriendRelation.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .friendId(friendId)
                .type(type)
                .build();
        return repository.save(FriendRelationMapper.INSTANCE.toFriendRelationDao(relation)).getId();
    }

    public List<FriendRelation> listFriends(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(FriendRelationMapper.INSTANCE::toFriendRelation)
                .toList();
    }

    public void removeFriend(UUID userId, UUID friendId) {
        repository.deleteByUserIdAndFriendId(userId, friendId);
    }

    public FriendRelation changeFriendType(UUID userId, UUID friendId, FriendshipType type) {
        var dao = repository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("Relation not found"));
        dao.setType(type);
        var saved = repository.save(dao);
        return FriendRelationMapper.INSTANCE.toFriendRelation(saved);
    }
}
