package com.memoritta.server.mapper;

import com.memoritta.server.dao.UserDao;
import com.memoritta.server.model.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    void shouldMapUserToUserDao() {
        // Given
        UUID id = UUID.randomUUID();
        String nickname = "TestNick";
        User user = new User();
        user.setId(id);
        user.setNickname(nickname);

        // When
        UserDao result = mapper.toUserDao(user);

        // Then
        assertNotNull(result);
        assertNotNull(result.getUser());
        assertEquals(id, result.getUser().getId());
        assertEquals(nickname, result.getUser().getNickname());
    }

    @Test
    void shouldMapUserDaoToUser() {
        // Given
        UUID id = UUID.randomUUID();
        String nickname = "MappedNick";
        User innerUser = new User();
        innerUser.setId(id);
        innerUser.setNickname(nickname);

        UserDao userDao = new UserDao();
        userDao.setUser(innerUser);

        // When
        User result = mapper.toUser(userDao);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(nickname, result.getNickname());
    }
}
