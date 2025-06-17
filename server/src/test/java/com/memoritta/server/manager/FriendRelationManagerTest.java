package com.memoritta.server.manager;

import com.memoritta.server.client.FriendRelationRepository;
import com.memoritta.server.dao.FriendRelationDao;
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
@ContextConfiguration(classes = {FriendRelationManagerTest.Config.class, FriendRelationManager.class})
class FriendRelationManagerTest {

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
    private FriendRelationManager manager;

    @BeforeEach
    void resetRepo() {
        reset(repository);
    }

    @Test
    void addFriend_shouldSaveAndReturnId() {
        UUID id = UUID.randomUUID();
        when(repository.save(any(FriendRelationDao.class)))
                .thenReturn(FriendRelationDao.builder().id(id).build());

        UUID result = manager.addFriend(UUID.randomUUID(), UUID.randomUUID(), FriendshipType.FRIENDS);

        assertThat(result).isEqualTo(id);
        verify(repository).save(any(FriendRelationDao.class));
    }

    @Test
    void listFriends_shouldMapDaoToDomain() {
        UUID userId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();
        UUID relationId = UUID.randomUUID();
        FriendRelationDao dao = FriendRelationDao.builder()
                .id(relationId)
                .userId(userId)
                .friendId(friendId)
                .type(FriendshipType.FRIENDS)
                .build();
        when(repository.findByUserId(userId)).thenReturn(List.of(dao));

        List<FriendRelation> result = manager.listFriends(userId);

        assertThat(result).hasSize(1);
        FriendRelation relation = result.getFirst();
        assertThat(relation.getId()).isEqualTo(relationId);
        assertThat(relation.getUserId()).isEqualTo(userId);
        assertThat(relation.getFriendId()).isEqualTo(friendId);
        assertThat(relation.getType()).isEqualTo(FriendshipType.FRIENDS);
    }
}
