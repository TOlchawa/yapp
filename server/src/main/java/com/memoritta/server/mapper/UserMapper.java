package com.memoritta.server.mapper;

import com.memoritta.server.dao.UserDao;
import com.memoritta.server.model.User;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "encryptedPassword", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    UserDao toUserDao(User user);
    User toUser(UserDao user);
}