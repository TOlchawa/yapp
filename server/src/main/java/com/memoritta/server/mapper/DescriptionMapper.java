package com.memoritta.server.mapper;

import com.memoritta.server.dao.DescriptionDao;
import com.memoritta.server.dao.ItemDao;
import com.memoritta.server.model.Description;
import com.memoritta.server.model.Item;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper
public interface DescriptionMapper {
    DescriptionMapper INSTANCE = Mappers.getMapper(DescriptionMapper.class);

    DescriptionDao toDescriptionDao(Description Description);
    Description toDescription(DescriptionDao DescriptionDao);
}