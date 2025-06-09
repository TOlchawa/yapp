package com.memoritta.server.mapper;

import com.memoritta.server.model.Item;
import com.memoritta.server.dao.ItemDao;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDao toItemDao(Item item);
    Item toItem(ItemDao itemDao);
}