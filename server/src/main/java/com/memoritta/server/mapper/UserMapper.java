package com.memoritta.server.mapper;

import com.memoritta.server.dao.UserDao;
import com.memoritta.server.model.User;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDao toUserDao(User user);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.email", target = "email")
    User toUser(UserDao user);
}