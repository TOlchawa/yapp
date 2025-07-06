package com.memoritta.server.controller;

import com.memoritta.server.client.FriendRelationRepository;
import com.memoritta.server.dao.FriendRelationDao;
import com.memoritta.server.manager.FriendRelationManager;
import com.memoritta.server.model.FriendRelation;
import com.memoritta.server.model.FriendshipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {FriendRelationControllerTest.Config.class, FriendRelationManager.class, FriendRelationController.class})
class FriendRelationControllerTest {

    @Configuration
    static class Config {
        @Bean
        FriendRelationRepository friendRelationRepository() {
            return mock(FriendRelationRepository.class);
        }
    }

    @Autowired
    private FriendRelationRepository repository;

    @Autowired
    private FriendRelationController controller;

    @BeforeEach
    void resetRepo() {
        reset(repository);
    }

    @Test
    void addFriend_shouldReturnId() {
        UUID id = UUID.randomUUID();
        when(repository.save(any(FriendRelationDao.class)))
                .thenReturn(FriendRelationDao.builder().id(id).build());

        UUID result = controller.addFriend(id.toString(), id.toString(), "FRIENDS");

        assertThat(result).isEqualTo(id);
    }

    @Test
    void listFriends_shouldReturnData() {
        UUID userId = UUID.randomUUID();
        when(repository.findByUserId(userId))
                .thenReturn(List.of(FriendRelationDao.builder().id(UUID.randomUUID()).userId(userId).type(FriendshipType.FRIENDS).build()));

        List<FriendRelation> result = controller.listFriends(userId.toString());

        assertThat(result).hasSize(1);
    }

    @Test
    void removeFriend_shouldCallRepository() {
        UUID userId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();

        controller.removeFriend(userId.toString(), friendId.toString());

        verify(repository).deleteByUserIdAndFriendId(userId, friendId);
    }

    @Test
    void changeFriendType_shouldReturnUpdated() {
        UUID userId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();
        FriendRelationDao dao = FriendRelationDao.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .friendId(friendId)
                .type(FriendshipType.FRIENDS)
                .build();
        when(repository.findByUserIdAndFriendId(userId, friendId)).thenReturn(java.util.Optional.of(dao));
        when(repository.save(any(FriendRelationDao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FriendRelation result = controller.changeFriendType(userId.toString(), friendId.toString(), "BEST_FRIENDS");

        assertThat(result.getType()).isEqualTo(FriendshipType.BEST_FRIENDS);
        verify(repository).save(any(FriendRelationDao.class));
    }
}
