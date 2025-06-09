package com.memoritta.server.mapper;

import com.memoritta.server.dao.ItemDao;
import com.memoritta.server.dao.PictureOfItemDao;
import com.memoritta.server.model.Item;
import com.memoritta.server.model.PictureOfItem;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@org.mapstruct.Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PictureOfItemMapper {
    PictureOfItemMapper INSTANCE = Mappers.getMapper(PictureOfItemMapper.class);
}