package com.memoritta.server.mapper;

import com.memoritta.server.dao.FriendRelationDao;
import com.memoritta.server.model.FriendRelation;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface FriendRelationMapper {
    FriendRelationMapper INSTANCE = Mappers.getMapper(FriendRelationMapper.class);

    FriendRelationDao toFriendRelationDao(FriendRelation relation);
    FriendRelation toFriendRelation(FriendRelationDao dao);
}
