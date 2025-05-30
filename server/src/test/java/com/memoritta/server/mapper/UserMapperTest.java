package com.memoritta.server.mapper;

import com.memoritta.server.dao.UserDao;
import com.memoritta.server.model.User;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    void shouldMapUserToUserDao() {
        // Given
        UUID id = UUID.randomUUID();
        String nickname = "TestNick";
        String email = "email@adres.com";
        User user = User.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .build();

        // When
        UserDao result = mapper.toUserDao(user);

        // Then
        assertThat(result).extracting("id", "nickname", "email")
                .containsExactly(id, nickname, email);
    }

    @Test
    void shouldMapUserDaoToUser() {
        // Given
        UUID id = UUID.randomUUID();
        String nickname = "MappedNick";
        String email = "email@adres.com";

        UserDao userDao = UserDao.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .build();

        // When
        User result = mapper.toUser(userDao);

        // Then
        assertThat(result).extracting("id", "nickname", "email")
                .containsExactly(id, nickname, email);
    }
}
